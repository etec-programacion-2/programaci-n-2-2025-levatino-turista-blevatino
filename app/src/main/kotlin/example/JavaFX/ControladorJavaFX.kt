package org.example.JavaFX

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.util.Callback
import kotlinx.coroutines.*
import org.example.ControladorPrincipal
import org.example.LugarTuristico
import org.example.Temporada

/**
 * Controlador de la Interfaz Gráfica de Usuario (JavaFX).
 * Delega la lógica de negocio al ControladorPrincipal y maneja la UI.
 */
class ControladorJavaFX(private val controladorPrincipal: ControladorPrincipal) {

    // Scope para manejar todas las corrutinas (operaciones asíncronas)
    // El Dispatchers.IO es adecuado para llamadas de red (Ktor)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Componentes de la UI
    private val lugaresListView = ListView<LugarTuristico>()
    private val chatArea = TextArea("¡Hola! Soy tu asistente turístico. Pregúntame lo que quieras.\n")
    private val chatInput = TextField()
    private val btnEnriquecer = Button("Enriquecer Descripción (IA)")

    /**
     * Crea y devuelve la escena principal de JavaFX con todos los componentes.
     */
    fun createScene(): Scene {

        // Configuración visual de la lista
        lugaresListView.cellFactory = Callback { _ -> LugarTuristicoCellFactory() }

        // --- 1. Panel de Recomendaciones (Izquierda) ---
        val temporadaSelector = ComboBox<Temporada>()
        temporadaSelector.items = FXCollections.observableArrayList(
            Temporada.entries.filter { it != Temporada.TODO_EL_ANO }
        )
        temporadaSelector.selectionModel.select(Temporada.VERANO)

        val btnMostrar = Button("Mostrar Lugares")
        btnMostrar.setOnAction {
            val temporada = temporadaSelector.selectionModel.selectedItem
            if (temporada != null) {
                mostrarRecomendaciones(temporada)
            }
        }

        btnEnriquecer.setOnAction { enriquecerLugarSeleccionado() }
        btnEnriquecer.isDisable = true

        // Habilita/deshabilita el botón de enriquecer según la selección
        lugaresListView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            btnEnriquecer.isDisable = newValue == null
        }

        val controlHBox = HBox(10.0, btnMostrar, btnEnriquecer).apply {
            alignment = Pos.CENTER_LEFT
        }

        val leftVBox = VBox(10.0, Label("Selecciona Temporada:"), temporadaSelector, controlHBox, lugaresListView).apply {
            padding = Insets(10.0)
            VBox.setVgrow(lugaresListView, Priority.ALWAYS)
        }

        // --- 2. Panel de Chat (Derecha) ---
        chatArea.isEditable = false
        chatArea.setWrapText(true)

        chatInput.promptText = "Escribe tu pregunta aquí..."
        // Permite enviar presionando ENTER
        chatInput.setOnAction { enviarPreguntaAlAsistente(chatInput.text) }

        val btnEnviar = Button("Enviar")
        btnEnviar.setOnAction { enviarPreguntaAlAsistente(chatInput.text) }

        val inputHBox = HBox(10.0, chatInput, btnEnviar).apply {
            alignment = Pos.CENTER_LEFT
            HBox.setHgrow(chatInput, Priority.ALWAYS)
        }

        val rightVBox = VBox(10.0, Label("Asistente IA (Chat con Memoria):"), chatArea, inputHBox).apply {
            padding = Insets(10.0)
            VBox.setVgrow(chatArea, Priority.ALWAYS)
        }

        // --- 3. Layout Principal ---
        val root = BorderPane().apply {
            left = leftVBox
            center = rightVBox
        }

        // Carga inicial al iniciar la aplicación
        Platform.runLater {
            mostrarRecomendaciones(Temporada.VERANO)
        }

        return Scene(root, 1000.0, 700.0)
    }

    // --- Lógica de Recomendaciones (Delegación) ---
    private fun mostrarRecomendaciones(temporada: Temporada) {
        val lugares = controladorPrincipal.solicitarRecomendaciones(temporada)
        lugaresListView.items = FXCollections.observableArrayList(lugares)
    }

    // --- Lógica del Asistente IA (Asíncrona para Chat) ---
    private fun enviarPreguntaAlAsistente(pregunta: String) {
        if (pregunta.isBlank()) return

        val userMessage = "Tú: $pregunta\n"
        chatArea.appendText(userMessage)
        chatInput.text = ""

        scope.launch {
            try {
                // Llamada a la red (IO Dispatcher)
                val respuesta = controladorPrincipal.obtenerRespuestaAsistente(pregunta)

                // Volver al hilo de la UI (Main Dispatcher) para actualizar chatArea
                withContext(Dispatchers.Main) {
                    chatArea.appendText("IA: $respuesta\n")
                }
            } catch (e: Exception) {
                // Manejo de errores de red o IA
                withContext(Dispatchers.Main) {
                    Alert(Alert.AlertType.ERROR, "Error al contactar con la IA: ${e.message}").showAndWait()
                }
            }
        }
    }

    // --- Lógica de Enriquecimiento (Asíncrona para Lugares) ---
    private fun enriquecerLugarSeleccionado() {
        val lugar = lugaresListView.selectionModel.selectedItem ?: return

        btnEnriquecer.isDisable = true // Deshabilitar mientras se procesa

        scope.launch {
            try {
                val nuevaDescripcionCompleta = controladorPrincipal.obtenerDescripcionEnriquecidaLugar(lugar)

                // Volver al hilo de la UI
                withContext(Dispatchers.Main) {
                    btnEnriquecer.isDisable = false

                    val mensaje: String
                    if (nuevaDescripcionCompleta.startsWith("PotenciadoIA:", true)) {
                        mensaje = "¡Éxito! Descripción de ${lugar.nombre} enriquecida por la IA."
                    } else if (nuevaDescripcionCompleta.startsWith("BaseDeDatos:", true)) {
                        mensaje = "La descripción de ${lugar.nombre} ya era óptima (Base de Datos)."
                    } else {
                        // Caso de fallo o respuesta inesperada (Aunque el controlador principal lo debería filtrar)
                        mensaje = "Error: El asistente IA devolvió una respuesta inesperada."
                    }

                    Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait()

                    // Forzar la actualización visual del elemento en la lista.
                    lugaresListView.refresh()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnEnriquecer.isDisable = false
                    Alert(Alert.AlertType.ERROR, "Error al enriquecer: ${e.message}").showAndWait()
                }
            }
        }
    }
}

// Clase helper para renderizar los elementos de la lista en JavaFX
private class LugarTuristicoCellFactory : ListCell<LugarTuristico>() {
    override fun updateItem(lugar: LugarTuristico?, empty: Boolean) {
        super.updateItem(lugar, empty)
        text = if (empty || lugar == null) {
            null
        } else {
            // Muestra la descripción completa del lugar
            "${lugar.nombre} (${lugar.ubicacion}) | Temporada: ${lugar.temporada.name}"
        }
    }
}