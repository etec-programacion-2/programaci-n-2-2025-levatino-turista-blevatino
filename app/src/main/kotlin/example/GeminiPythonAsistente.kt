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

@Serializable
data class PythonResponse(
    val respuesta: String? = null,
    val error: String? = null
)

@Serializable
data class RespuestaEnriquecimiento(
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
        // Configuración para usar kotlinx.serialization (JSON)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String {
        val peticion = PeticionChat(historial_mensajes = historial_mensajes)
        try {
            val response = client.post("$BASE_URL/chat") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            // 1. Manejo de error HTTP (4xx o 5xx)
            if (!response.status.isSuccess()) {
                val errorBody = try { response.body<PythonResponse>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error de servidor desconocido o respuesta no JSON."
                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            // 2. **Manejo de error de PARSING JSON (Causa de la última falla)**
            val respuestaData: PythonResponse = try {
                response.body()
            } catch (e: Exception) {
                val rawBody = response.body<String>() // Intenta leer como String para obtener la causa
                // Lanza un error de I/O para que sea capturado por el ControladorPrincipal y propagado a la GUI
                throw IOException("Error de PARSING JSON. Cuerpo devuelto: $rawBody. Error: ${e.message}")
            }
            // -------------------------------------------------------------

            // 3. Verifica el contenido de la respuesta
            return respuestaData.respuesta
                ?: throw Exception("La IA devolvió un JSON 200, pero el campo 'respuesta' es nulo o ausente.")
        } catch (e: Exception) {
            // Relanza como IOException si es un error de red o parsing para la capa superior
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
                val errorBody = try { response.body<RespuestaEnriquecimiento>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error de servidor desconocido o respuesta no JSON."
                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            // 2. **Manejo de error de PARSING JSON**
            val respuestaData: RespuestaEnriquecimiento = try {
                response.body()
            } catch (e: Exception) {
                val rawBody = response.body<String>()
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
