package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.io.IOException

// Clase que encapsula la configuración de plugins y las rutas del servidor Ktor.
class KtorSetup(private val controlador: ControladorPrincipal) {

    // Configura los plugins principales de la aplicación Ktor.
    fun configureApplicationModule(application: Application) {
        // --- Configuración de Plugins ---
        application.install(CallLogging) {
            level = Level.INFO
        }
        application.install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        application.install(CORS) {
            anyHost()
            allowHeader(io.ktor.http.HttpHeaders.ContentType)
            allowMethod(HttpMethod.Post)
        }

        // --- Configuración de Rutas ---
        application.routing {
            configureApiRoutes(this)
            configureStaticRoutes(this)
        }
    }

    // Define las rutas de la API.
    private fun configureApiRoutes(routing: Routing) {
        routing.route("/api") {

            // 1. Obtener la lista de temporadas disponibles
            get("/temporadas") {
                val temporadas = Temporada.entries
                    .filter { it != Temporada.TODO_EL_ANO }
                    .map { mapOf("value" to it.name, "display" to it.nombreDisplay) }

                call.respond(temporadas)
            }

            // 2. Obtener Lugares filtrados por Temporada
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

            // 3. Chat con IA
            post("/chat") {
                val peticion = call.receive<PeticionChat>()

                try {
                    val respuesta = controlador.solicitarRespuestaChat(peticion.historial_mensajes)
                    call.respond(Mensaje(role = "assistant", content = respuesta))
                } catch (e: IOException) {
                    call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "Error de comunicación con el servicio de IA."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al comunicarse con la IA: ${e.message}"))
                }
            }

            // 4. Enriquecer un lugar por ID
            post("/enriquecer/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val lugar = controlador.servicioRecomendaciones.obtenerLugarPorId(id)

                if (lugar == null) {
                    return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lugar no encontrado."))
                }

                try {
                    // El controlador gestiona el enriquecimiento y devuelve el objeto modificado.
                    val lugarModificado = controlador.enriquecerDescripcionLugar(lugar)

                    call.respond(lugarModificado)
                } catch (e: IOException) {
                    call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "Error de comunicación con el servicio de IA."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al enriquecer con IA: ${e.message}"))
                }
            }
        }
    }

    // Sirve el contenido estático (HTML/CSS/JS).
    private fun configureStaticRoutes(routing: Routing) {
        // Archivos en src/main/resources/static
        routing.staticResources("/", "static") {
            default("index.html")
        }
    }
}