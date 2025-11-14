package org.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking

// Punto de entrada principal de la aplicación Ktor.
fun main() {
    // 1. Inicialización de Capas (Inyección de Dependencias)
    val repository = JsonLugarTuristicoRepository()
    val servicioRecomendaciones = ServicioRecomendaciones(repository)
    val asistente = GeminiPythonAsistente()
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistente)

    // Opcional: Descomentar para probar solo la consola.
    // runBlocking { VistaConsola(controlador).iniciar() }

    // 2. Configuración e Inicio del Servidor Ktor
    val setup = KtorSetup(controlador)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        setup.configureApplicationModule(this)
    }.start(wait = true)
}