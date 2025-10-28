package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Cliente para comunicarse con el servidor Python local que ejecuta la IA.
 */
class AsistenteQwenService {

    private val client = HttpClient(CIO) {
        // Configuración para usar kotlinx.serialization (JSON)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignora campos desconocidos si la IA añade algo extra
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // Usar la IP para el emulador Android (10.0.2.2) o la IP local (127.0.0.1) si se ejecuta en PC
    private val BASE_URL = "http://10.0.2.2:5000"

    /**
     * Llama al servidor Python para enriquecer la descripción de un lugar.
     * * @param nombre El nombre del lugar turístico.
     * @param descripcion La descripción actual del lugar.
     * @return La nueva descripción enriquecida por la IA.
     */
    suspend fun enriquecerDescripcion(nombre: String, descripcion: String): String {

        // 1. Prepara el cuerpo de la petición usando el modelo de datos
        val peticion = PeticionEnriquecimiento(
            lugar_nombre = nombre,
            descripcion_actual = descripcion
        )

        try {
            // 2. Realiza la petición POST a la ruta /ask
            val response = client.post("$BASE_URL/ask") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }

            // 3. Verifica si hubo un error HTTP (4xx o 5xx)
            if (!response.status.isSuccess()) {
                // Intenta leer el mensaje de error del cuerpo
                val errorBody = response.body<RespuestaEnriquecimiento>()
                throw Exception("Error HTTP ${response.status.value}: ${errorBody.error ?: "Error de servidor desconocido."}")
            }

            // 4. Parsea la respuesta JSON
            val respuestaData = response.body<RespuestaEnriquecimiento>()

            // 5. Retorna la descripción enriquecida
            return respuestaData.respuesta ?: throw Exception("La IA no devolvió respuesta, sino un error: ${respuestaData.error}")

        } catch (e: Exception) {
            // Manejo de errores de red o parsing
            throw Exception("Fallo en la comunicación con el servidor Python: ${e.message}")
        }
    }
}