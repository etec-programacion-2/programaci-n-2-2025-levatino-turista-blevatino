package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import kotlinx.serialization.Serializable
import java.io.File

// --- Clases de Datos Específicas para las Peticiones Web ---

@Serializable
data class LugaresRequest(val temporada: String)
@Serializable
data class EnriquecerRequest(val id: Int)


/**
 * Define las rutas (endpoints) de la API para la aplicación.
 * @param controlador La instancia única del ControladorPrincipal.
 */
fun Routing.apiRoutes(controlador: ControladorPrincipal) {

    route("/api") {

        // 1. Obtener la lista de temporadas (para ComboBox/Select)
        get("/temporadas") {
            val temporadas = Temporada.entries
                .filter { it != Temporada.TODO_EL_ANO }
                .map { mapOf("value" to it.name, "display" to it.nombreDisplay) }

            call.respond(temporadas)
        }

        // 2. Obtener Lugares por Temporada
        get("/lugares/{temporada}") {
            val temporadaStr = call.parameters["temporada"]?.uppercase()
            val temporada = try {
                Temporada.valueOf(temporadaStr ?: "")
            } catch (e: IllegalArgumentException) {
                return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Temporada no válida.")
                )
            }

            val lugares = controlador.solicitarRecomendaciones(temporada)
            call.respond(lugares)
        }

        // 3. Chatear con la IA (Requiere recibir el historial completo desde el frontend)
        post("/chat") {
            // Recibe el historial de mensajes del cliente
            val peticionChat = try {
                call.receive<PeticionChat>()
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato de chat inválido."))
            }

            try {
                // Aquí usamos el asistente directamente si el controlador no maneja el estado
                // La función obtenerRespuesta debe encargarse de procesar la lista y devolver el mensaje
                val respuesta = controlador.asistenteIA.obtenerRespuesta(peticionChat.historial_mensajes)

                // Devuelve la respuesta como un mensaje de la IA
                call.respond(Mensaje(role = "assistant", content = respuesta))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al comunicarse con la IA: ${e.message}"))
            }
        }

        // 4. Enriquecer un lugar (Recibe el ID y devuelve el objeto modificado)
        post("/enriquecer/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val lugar = controlador.servicioRecomendaciones.obtenerLugarPorId(id) // Necesitas agregar obtenerLugarPorId al Servicio

            if (lugar == null) {
                return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lugar no encontrado."))
            }

            try {
                // Modifica directamente la descripción del objeto 'lugar' en memoria
                controlador.enriquecerDescripcionLugar(lugar)

                // Devuelve el objeto LugarTuristico modificado
                call.respond(lugar)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al enriquecer con IA: ${e.message}"))
            }
        }
    }
}

/**
 * Sirve los archivos estáticos (HTML, CSS, JS).
 */
fun Routing.staticRoutes() {
    // Si tienes una carpeta 'resources/static'
    static("/") {
        staticRootFolder = File("src/main/resources/static")
        files(".")
        default("index.html") // Sirve index.html por defecto
    }
}