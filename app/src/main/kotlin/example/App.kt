package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

/**
 * Punto de entrada principal de la aplicación Ktor.
 *
 * Contiene la lógica para:
 * 1. Inicializar todas las capas (Repository, Servicio, Asistente, Controlador).
 * 2. Iniciar el servidor web Netty.
 */
fun main() {
    // --- 1. Inicialización de Capas de Datos y Servicios ---

    // Repositorio (Capa de Datos): Lee desde 'lugares.json'
    val repository = JsonLugarTuristicoRepository()

    // Servicio de Negocio: Utiliza el Repositorio para la lógica de filtrado.
    val servicioRecomendaciones = ServicioRecomendaciones(repository)

    // Asistente IA: Implementación que se comunica con el servidor Python (vía HTTP)
    val asistente = GeminiPythonAsistente()

    // Controlador: Une el Servicio de Negocio y el Asistente IA
    // SOLUCIÓN al error 'ControladorPrincipal(repository, asistente)'
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistente)

    // 2. Iniciar el servidor Ktor
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(controlador)
    }.start(wait = true)
}

/**
 * Define la configuración de plugins y las rutas del servidor Ktor.
 */
fun Application.module(controlador: ControladorPrincipal) {

    // --- Configuración de Plugins ---

    // 1. Call Logging: Muestra las peticiones HTTP en la consola
    install(CallLogging) {
        level = Level.INFO
    }

    // 2. Content Negotiation: Permite enviar y recibir JSON
    install(ContentNegotiation) {
        json(Json {
            // Configuración para manejar JSONs complejos o con datos faltantes
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // 3. CORS: Permite peticiones desde navegadores (necesario si el frontend es servido por separado)
    install(CORS) {
        anyHost() // Permite cualquier host (para desarrollo)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
    }

    // --- Configuración de Rutas ---
    routing {
        // Rutas de la API (endpoints como /api/lugares, /api/chat)
        apiRoutes(controlador)

        // Servicio de Contenido Estático (Frontend: HTML, CSS, JS)
        // Usamos staticResources para asegurar que los archivos se carguen desde el Classpath (carpeta 'resources').
        staticResources("/", "static") {
            // La carpeta 'static' debe estar dentro de 'resources'
            default("index.html") // Archivo que se sirve por defecto al acceder a la raíz
        }
    }
}
