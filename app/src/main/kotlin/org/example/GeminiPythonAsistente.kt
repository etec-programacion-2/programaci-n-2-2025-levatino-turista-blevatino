package org.example

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.json.Json
import java.io.IOException


class GeminiPythonAsistente(private val pythonServiceUrl: String) : AsistenteIA {

    // Cliente de OkHttp para gestionar las peticiones HTTP.
    private val client = OkHttpClient()
    // Objeto para manejar la serialización/deserialización de JSON.
    private val json = Json { ignoreUnknownKeys = true }
    // Define el tipo de contenido de la petición como JSON.
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Envía una pregunta a tu servidor de Python y procesa la respuesta.
     * La lógica de comunicación con la API externa está encapsulada aquí.
     *
     * @param pregunta La pregunta de texto para la IA.
     * @return La respuesta de la IA como un String.
     */
    override fun obtenerRespuesta(pregunta: String): String {
        // Construye el JSON que se enviará en el cuerpo de la petición.
        val jsonBody = """
        {
          "pregunta": "$pregunta"
        }
        """.trimIndent()

        val requestBody = jsonBody.toRequestBody(mediaType)

        // Crea la petición HTTP POST a la URL de tu servicio de Python.
        val request = Request.Builder()
            .url(pythonServiceUrl)
            .post(requestBody)
            .build()

        // Envía la petición y procesa la respuesta de forma síncrona.
        try {
            client.newCall(request).execute().use { response ->
                // Si la petición no fue exitosa (código 200), lanza una excepción.
                if (!response.isSuccessful) {
                    throw IOException("Respuesta del servidor inesperada: ${response.code}")
                }

                // Lee el cuerpo de la respuesta.
                val responseBody = response.body?.string() ?: throw IOException("Cuerpo de la respuesta vacío")

                // Convierte la cadena JSON en nuestro objeto PythonResponse.
                val pythonResponse = json.decodeFromString<PythonResponse>(responseBody)

                // Devuelve la respuesta o un mensaje de error si el servidor lo envió.
                return pythonResponse.respuesta ?: pythonResponse.error ?: "Respuesta inesperada del servidor."
            }
        } catch (e: Exception) {
            // Maneja cualquier error de red o de comunicación.
            System.err.println("Error al comunicarse con el servidor de Python: ${e.message}")
            return "Lo siento, no pude obtener una respuesta de la IA."
        }
    }
}
