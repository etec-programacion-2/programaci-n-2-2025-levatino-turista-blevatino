package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Implementación de la interfaz AsistenteIA.
 * Se comunica con el servidor Python (Flask + OpenRouter/Mixtral) vía HTTP usando Ktor.
 */
class GeminiPythonAsistente : AsistenteIA {

    // URL base del servidor Python.
    private val BASE_URL = "http://127.0.0.1:5000"
    // Usar "http://127.0.0.1:5000" si ejecutas Kotlin en tu máquina local (JVM).

    private val client = HttpClient(CIO) {
        // Configuración para usar kotlinx.serialization (JSON)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * Función de chat genérico. Ahora es 'suspend' para coincidir con la interfaz.
     */
    override suspend fun obtenerRespuesta(pregunta: String): String { // <-- CORRECCIÓN APLICADA AQUÍ
        return "La funcionalidad de chat genérico no está implementada en esta versión del servidor de enriquecimiento."
    }

    /**
     * Llama al servidor Python para enriquecer la descripción de un lugar.
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

            return respuestaData.respuesta
                ?: throw Exception("La respuesta de la IA no contenía el campo 'respuesta'. Detalles: ${respuestaData.error}")

        } catch (e: Exception) {
            if (e is IOException) throw e
            throw IOException("Fallo en la comunicación con el servidor Python o en el parsing JSON: ${e.message}")
        }
    }
}