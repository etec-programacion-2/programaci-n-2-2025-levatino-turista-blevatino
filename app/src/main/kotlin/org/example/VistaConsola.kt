package org.example

class VistaConsola(private val controlador: ControladorPrincipal) {

    fun iniciar() {
        while (true) {
            mostrarMenu()
            when (val opcion = leerOpcion()) {
                1 -> solicitarRecomendacionesPorTemporada()
                2 -> hacerPreguntaAlAsistente()
                3 -> {
                    println("Saliendo de la aplicación.")
                    return
                }
                else -> println("Opción no válida. Por favor, intente de nuevo.")
            }
        }
    }

    private fun mostrarMenu() {
        println("\n--- Bienvenido al Asistente de Viajes ---")
        println("1. Ver lugares por temporada")
        println("2. Preguntar a la IA sobre un lugar")
        println("3. Salir")
        print("Seleccione una opción: ")
    }

    private fun leerOpcion(): Int {
        return try {
            readln().toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun solicitarRecomendacionesPorTemporada() {
        print("Ingrese la temporada (VERANO, OTONO, INVIERNO, PRIMAVERA): ")
        val temporada = readln()
        val lugares = controlador.solicitarRecomendaciones(temporada)
        if (lugares.isNotEmpty()) {
            println("--- Lugares recomendados para $temporada ---")
            lugares.forEach { lugar ->
                println("  - ${lugar.nombre}: ${lugar.descripcion}")
            }
        } else {
            println("No se encontraron lugares para esa temporada o la entrada no es válida.")
        }
    }

    private fun hacerPreguntaAlAsistente() {
        print("Escriba su pregunta para la IA: ")
        val pregunta = readln()
        val respuesta = controlador.preguntarAlAsistente(pregunta)
        println("--- Respuesta del Asistente IA ---")
        println(respuesta)
    }
}
