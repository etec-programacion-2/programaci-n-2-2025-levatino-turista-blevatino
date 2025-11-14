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

// --- Clases de Datos para Respuestas ---

// Clase que maneja la respuesta del servidor Python.
@Serializable
data class PythonBaseResponse(
    val respuesta: String? = null,
    val error: String? = null
)


// --- Cliente Ktor ---

// Implementación de AsistenteIA que se comunica con el servidor Python (Flask) vía HTTP.
class GeminiPythonAsistente : AsistenteIA {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // Lanza excepción para códigos de estado 4xx/5xx
        expectSuccess = true
    }

    // URL del microservicio Python (asume puerto 5000)
    private val BASE_URL = "http://127.0.0.1:5000"

    // Llama al endpoint /chat.
    override suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String {
        val peticion = PeticionChat(historial_mensajes)
        val endpoint = "$BASE_URL/chat"

        return executePostRequest(endpoint, peticion)
    }

    // Llama al endpoint /ask.
    override suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String {
        val peticion = PeticionEnriquecimiento(nombre, descripcion)
        val endpoint = "$BASE_URL/ask"

        return executePostRequest(endpoint, peticion)
    }

    // Función genérica para manejar la comunicación con el servidor Python.
    private suspend fun executePostRequest(endpoint: String, peticion: Any): String {
        try {
            val response = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            val respuestaData: PythonBaseResponse = try {
                response.body()
            } catch (e: Exception) {
                val rawBody = response.body<String>()
                throw IOException("Error de PARSING JSON. Cuerpo devuelto: $rawBody. Error: ${e.message}")
            }

            // Verifica que el campo 'respuesta' exista
            return respuestaData.respuesta
                ?: throw Exception("La respuesta de la IA no contenía el campo 'respuesta'. Error: ${respuestaData.error}")
        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python en $endpoint: ${e.message}")
        }
    }
}