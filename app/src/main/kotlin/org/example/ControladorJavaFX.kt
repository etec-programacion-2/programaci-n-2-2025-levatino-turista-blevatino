package org.example

import javafx.scene.control.TextArea

class ControladorJavaFX(private val controladorPrincipal: ControladorPrincipal) {

    fun mostrarRecomendaciones(temporadaStr: String, areaTexto: TextArea) {
        areaTexto.clear()

        try {
            val temporada = Temporada.valueOf(temporadaStr) // Conversión segura
            val lugares = controladorPrincipal.solicitarRecomendaciones(temporada)

            if (lugares.isNotEmpty()) {
                val resultados = lugares.joinToString("\n") {
                    " - ${it.nombre} (${it.ubicacion}): ${it.descripcion}"
                }
                areaTexto.text = "--- Recomendaciones para $temporadaStr ---\n$resultados"
            } else {
                areaTexto.text = "No se encontraron lugares para la temporada $temporadaStr."
            }
        } catch (e: IllegalArgumentException) {
            areaTexto.text = "Error: La temporada seleccionada no es válida."
        }
    }

    fun hacerPregunta(pregunta: String, areaRespuesta: TextArea) {
        if (pregunta.isBlank()) {
            areaRespuesta.text = "Por favor, escribe una pregunta."
            return
        }

        areaRespuesta.text = "Consultando a la IA, espere por favor..."

        // Nota: En un entorno de producción JavaFX, esta llamada debería estar en un hilo
        // separado para evitar congelar la interfaz, pero para la demostración funciona.
        try {
            val respuesta = controladorPrincipal.obtenerRespuestaAsistente(pregunta)
            areaRespuesta.text = "--- Respuesta de la IA ---\n$respuesta"
        } catch (e: Exception) {
            areaRespuesta.text = "Error al comunicarse con el Asistente IA. Asegúrese de que el servidor de Python esté ejecutándose. Error: ${e.message}"
        }
    }
}