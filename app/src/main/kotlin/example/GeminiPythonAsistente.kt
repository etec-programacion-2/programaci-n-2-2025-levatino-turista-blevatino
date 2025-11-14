package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

// --- Clases de Datos para Peticiones ---

@Serializable
data class Mensaje(
    val role: String, // "user" o "assistant"
    val content: String
)

@Serializable
data class PeticionChat(
    val historial_mensajes: List<Mensaje>
)

@Serializable
data class PeticionEnriquecimiento(
    val lugar_nombre: String,
    val descripcion_actual: String
)

// --- Clases de Datos para Respuestas (Deben coincidir con Flask) ---

/**
 * [CORRECCIÓN] Clase unificada para manejar la respuesta del servidor Python (Flask),
 * ya sea para chat o enriquecimiento. El servidor Python siempre debe devolver una
 * respuesta con la clave 'respuesta' o 'error'.
 */
@Serializable
data class PythonBaseResponse(
    val respuesta: String? = null,
    val error: String? = null
)


// --- Cliente Ktor ---

/**
 * Implementación de AsistenteIA que se comunica con el servidor Python (Flask) vía HTTP usando Ktor.
 */
class GeminiPythonAsistente : AsistenteIA {

    // **IMPORTANTE: Usar la IP local si Flask se ejecuta en la misma máquina**
    private val BASE_URL = "http://127.0.0.1:5000"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        // Configuración para reintentos o tiempo de espera
    }

    override suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String {
        val peticion = PeticionChat(historial_mensajes = historial_mensajes)

        try {
            val response = client.post("$BASE_URL/chat") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            // 1. Manejo de error HTTP (ej. 404, 500)
            if (!response.status.isSuccess()) {
                val errorBody = try { response.body<PythonBaseResponse>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error de servidor desconocido o respuesta no JSON."
                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            // 2. Manejo de error de PARSING JSON (Lo que causaba tu error)
            val respuestaData: PythonBaseResponse = try {
                response.body()
            } catch (e: Exception) {
                val rawBody = response.body<String>()
                // [DIAGNÓSTICO] Esta línea imprimirá en la consola Ktor el cuerpo que vino de Python
                println("--- DIAGNÓSTICO KTOR: FALLO CRÍTICO DE PARSING ---")
                println("Cuerpo crudo devuelto por Flask: '$rawBody'")
                println("--------------------------------------------------")
                throw IOException("Error de PARSING JSON. Cuerpo devuelto: $rawBody. Error: ${e.message}")
            }

            // 3. Verifica el contenido de la respuesta (la clave 'respuesta')
            return respuestaData.respuesta
                ?: throw Exception("La respuesta de la IA no contenía el campo 'respuesta'. Detalles: ${respuestaData.error}")
        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python: ${e.message}")
        }
    }

    override suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String {
        val peticion = PeticionEnriquecimiento(lugar_nombre = nombre, descripcion_actual = descripcion)
        try {
            val response = client.post("$BASE_URL/ask") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            // 1. Manejo de error HTTP
            if (!response.status.isSuccess()) {
                val errorBody = try { response.body<PythonBaseResponse>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error de servidor desconocido o respuesta no JSON."
                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            // 2. Manejo de error de PARSING JSON
            val respuestaData: PythonBaseResponse = try {
                response.body()
            } catch (e: Exception) {
                val rawBody = response.body<String>()
                println("--- DIAGNÓSTICO KTOR: FALLO CRÍTICO DE PARSING ---")
                println("Cuerpo crudo devuelto por Flask: '$rawBody'")
                println("--------------------------------------------------")
                throw IOException("Error de PARSING JSON. Cuerpo devuelto: $rawBody. Error: ${e.message}")
            }

            // 3. Verifica el contenido de la respuesta
            return respuestaData.respuesta
                ?: throw Exception("La respuesta de la IA no contenía el campo 'respuesta'. Detalles: ${respuestaData.error}")
        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python: ${e.message}")
        }
    }
}