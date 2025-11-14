package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.IOException

/**
 * [REQUISITO 3: ESTRUCTURA]
 * Clase que encapsula la definición de todas las rutas de la API y estáticas.
 * Sustituye a las funciones de extensión 'fun Routing.apiRoutes' y 'fun Routing.staticRoutes'.
 */
class ApiRouteSetup(private val controlador: ControladorPrincipal) {

    /**
     * Configura todas las rutas de la API.
     */
    fun configureApiRoutes(routing: Routing) {
        routing.route("/api") {

            // 1. Obtener la lista de temporadas
            get("/temporadas") { /* ... Lógica anterior ... */ }

            // 2. Obtener Lugares por Temporada
            get("/lugares/{temporada}") { /* ... Lógica anterior ... */ }

            // 3. Chat con IA
            post("/chat") { /* ... Lógica anterior ... */ }

            // 4. Enriquecer un lugar
            post("/enriquecer/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val lugar = controlador.servicioRecomendaciones.obtenerLugarPorId(id)

                if (lugar == null) {
                    return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lugar no encontrado."))
                }

                try {
                    // Llama al controlador (que ahora devuelve el objeto modificado)
                    val lugarModificado = controlador.enriquecerDescripcionLugar(lugar)
                    call.respond(lugarModificado)
                } catch (e: IOException) {
                    call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "Error de comunicación con el servicio de IA."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al enriquecer con IA."))
                }
            }
        }
    }

    /**
     * Configura el servicio de archivos estáticos (Frontend).
     */
    fun configureStaticRoutes(routing: Routing) {
        routing.staticResources("/", "static") {
            default("index.html")
        }
    }
}