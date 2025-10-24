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

// --- Clases de Datos para el Chat con Memoria ---

/**
 * Representa un mensaje individual en la conversación (Historial).
 * Coincide con el formato JSON que espera OpenAI/OpenRouter: {"role": "user"|"assistant", "content": "texto"}
 */


/**
 * Representa la estructura de la petición JSON para el chat contextual con memoria.
 * El servidor Flask espera { "historial_mensajes": [...] }
 */

// --- Cliente Ktor ---

/**
 * Implementación de la interfaz AsistenteIA.
 * Se comunica con el servidor Python (Flask + OpenRouter) vía HTTP usando Ktor.
 */
class GeminiPythonAsistente : AsistenteIA {

    // Cambia la IP según tu entorno:
    // - JVM Local: "http://127.0.0.1:5000"
    // - Emulador Android: "http://10.0.2.2:5000"
    private val BASE_URL = "http://127.0.0.1:5000"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * Implementa la opción 2 del menú: Chat contextual con memoria (llama a la ruta /chat).
     * @param historial_mensajes La lista completa de mensajes de la conversación, incluyendo el mensaje actual del usuario.
     */
    override suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String {
        // CAMBIO: Ahora enviamos el objeto PeticionChat que contiene el historial.
        val peticion = PeticionChat(historial_mensajes = historial_mensajes)

        try {
            val response = client.post("$BASE_URL/chat") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            if (!response.status.isSuccess()) {
                val errorBody = try { response.body<RespuestaEnriquecimiento>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error del servidor de chat desconocido."
                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            val respuestaData = response.body<RespuestaEnriquecimiento>()

            return respuestaData.respuesta
                ?: throw Exception("Respuesta de chat vacía o inconsistente. Detalles: ${respuestaData.error}")

        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python o en el parsing JSON (chat): ${e.message}")
        }
    }

    /**
     * Implementa la opción 3 del menú: Enriquecer descripción (llama a la ruta /ask).
     */
    override suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String {

        val peticion = PeticionEnriquecimiento(
            lugar_nombre = nombre,
            descripcion_actual = descripcion
        )

        try {
            val response = client.post("$BASE_URL/ask") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            if (!response.status.isSuccess()) {
                val errorBody = try { response.body<RespuestaEnriquecimiento>() } catch (e: Exception) { null }
                val errorMessage = errorBody?.error ?: "Error de servidor desconocido o respuesta no JSON."

                throw IOException("Error HTTP ${response.status.value}: $errorMessage")
            }

            val respuestaData = response.body<RespuestaEnriquecimiento>()

            // La respuesta contiene el prefijo de etiquetado
            return respuestaData.respuesta
                ?: throw Exception("La respuesta de la IA no contenía el campo 'respuesta'. Detalles: ${respuestaData.error}")

        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python o en el parsing JSON: ${e.message}")
        }
    }
}

