package org.example

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

/**
 * [REQUISITO 3: ESTRUCTURA]
 * Clase que encapsula la configuración de plugins y módulos de Ktor.
 * Sustituye a la función de extensión 'fun Application.module'.
 */
class KtorConfigurator(private val controlador: ControladorPrincipal) {

    /**
     * Configura los plugins de la aplicación.
     */
    fun configureApplicationModule(application: Application) {
        // --- Configuración de Plugins (CallLogging, ContentNegotiation, CORS) ---
        application.install(CallLogging) { level = Level.INFO }
        application.install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        application.install(CORS) {
            anyHost()
            // ... (otras configuraciones de headers/methods)
        }

        // 4. Configuración de Rutas (Usa la clase ApiRouteSetup)
        application.routing {
            val routeSetup = ApiRouteSetup(controlador) // Instancia la clase de rutas
            routeSetup.configureApiRoutes(this) // Rutas de la API
            routeSetup.configureStaticRoutes(this) // Servicio de Contenido Estático
        }
    }
}