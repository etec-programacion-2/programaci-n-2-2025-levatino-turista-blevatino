package org.example.JavaFX

import javafx.application.Application
import javafx.stage.Stage
import org.example.ControladorPrincipal
import org.example.GeminiPythonAsistente
import org.example.JsonLugarTuristicoRepository
import org.example.ServicioRecomendaciones

class MainJavaFX : Application() {

    override fun start(stage: Stage) {
        try {
            // --- 1. Inicialización de la Arquitectura (Inyección de Dependencias) ---

            // Repositorio
            val repository = JsonLugarTuristicoRepository()

            // Asistente IA (Ktor -> Python/Flask)
            val asistente = GeminiPythonAsistente()

            // Servicio de Negocio
            val servicio = ServicioRecomendaciones(repository)

            // Controlador de Negocio (Orquestador central)
            val controladorPrincipal = ControladorPrincipal(servicio, asistente)

            // --- 2. Iniciar la Vista de JavaFX ---
            val controladorJavaFX = ControladorJavaFX(controladorPrincipal)

            stage.title = "Asistente Turístico IA - ETEC"
            // Crea la escena usando el controlador
            stage.scene = controladorJavaFX.createScene()
            stage.show()

        } catch (e: Exception) {
            System.err.println("FATAL ERROR AL INICIAR JAVAFX: ${e.message}")
            e.printStackTrace()
            // Se puede mostrar una alerta simple para el usuario si es un error fatal
            // Alert(Alert.AlertType.ERROR, "Fallo crítico al iniciar la aplicación: ${e.message}").showAndWait()
        }
    }
}

/**
 * Función principal que llama al método de lanzamiento de JavaFX.
 */
fun main() {
    Application.launch(MainJavaFX::class.java)
}