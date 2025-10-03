package org.example

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*

class MainJavaFX : Application() {

    override fun start(primaryStage: Stage) {
        // --- Inyección de Dependencias (Mismo proceso que en app.kt) ---

        // 1. Configuración de dependencias
        val pythonServiceUrl = "http://127.0.0.1:5000/ask"
        val resource = object {}.javaClass.classLoader.getResource("lugares.json")
            ?: throw IllegalArgumentException("Error fatal: Archivo 'lugares.json' no encontrado.")
        val jsonFilePath = resource.toURI().path

        // 2. Inicialización de capas
        val asistenteIA = GeminiPythonAsistente(pythonServiceUrl)
        val repositorio = JsonLugarTuristicoRepository(jsonFilePath)
        val servicioRecomendaciones = ServicioRecomendaciones(repositorio)
        val controladorPrincipal = ControladorPrincipal(servicioRecomendaciones, asistenteIA)

        // 3. Crear el controlador de la GUI, inyectando el ControladorPrincipal existente.
        val controladorGUI = ControladorJavaFX(controladorPrincipal)

        // --- Configuración de la Ventana (UI) ---
        val root = VBox(10.0) // Contenedor principal
        root.padding = javafx.geometry.Insets(15.0)

        // Controles de Recomendaciones por Temporada
        val temporadaLabel = Label("Selecciona una Temporada:")
        val temporadaSelector = ChoiceBox(javafx.collections.FXCollections.observableArrayList(
            Temporada.entries.map { it.name }.plus("TODO_EL_ANO")
        ))
        val recomendacionArea = TextArea()
        recomendacionArea.isEditable = false
        recomendacionArea.prefRowCount = 10

        // Controles de Asistente IA
        val iaLabel = Label("Pregunta al Asistente IA:")
        val iaInput = TextField()
        iaInput.promptText = "¿Cuál es el lugar turístico más popular de Mendoza?"
        val iaOutput = TextArea()
        iaOutput.isEditable = false
        iaOutput.prefRowCount = 5

        val preguntarBoton = Button("Preguntar a la IA")

        // --- Conexión de Eventos ---

        // Evento para cambiar la temporada
        temporadaSelector.setOnAction {
            val temporada = temporadaSelector.value
            if (temporada != null) {
                controladorGUI.mostrarRecomendaciones(temporada, recomendacionArea)
            }
        }

        // Evento para hacer preguntas a la IA
        preguntarBoton.setOnAction {
            controladorGUI.hacerPregunta(iaInput.text, iaOutput)
        }

        // Organización del Layout
        root.children.addAll(
            temporadaLabel, temporadaSelector, recomendacionArea,
            Separator(), // Separador visual
            iaLabel, iaInput, preguntarBoton, iaOutput
        )

        val scene = Scene(root, 600.0, 700.0)
        primaryStage.title = "Asistente de Viajes JavaFX"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(MainJavaFX::class.java)
}