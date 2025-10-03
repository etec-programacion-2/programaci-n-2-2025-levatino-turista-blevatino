package org.example

import java.lang.IllegalArgumentException

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
        print("Ingrese la temporada (VERANO, OTONO, INVIERNO, PRIMAVERA, TODO_EL_ANO): ")
        val temporadaIngresada = readln()

        try {
            // 1. Convertimos la entrada String a MAYÚSCULAS y luego al enum Temporada.
            val temporada = Temporada.valueOf(temporadaIngresada.uppercase())

            // 2. Llamamos al controlador con el tipo de dato correcto (Temporada).
            val lugares = controlador.solicitarRecomendaciones(temporada)

            if (lugares.isNotEmpty()) {
                println("--- Lugares recomendados para ${temporada.name} ---")
                lugares.forEach { lugar ->
                    println("  - ${lugar.nombre}: ${lugar.descripcion}")
                }
            } else {
                println("No se encontraron lugares para esa temporada o la entrada no es válida.")
            }
        } catch (e: IllegalArgumentException) {
            // Manejamos el error si el usuario ingresa algo que no es un valor de Temporada.
            println("Error: La temporada ingresada no es válida. Por favor, intente con una de las opciones sugeridas.")
        }
    }

    private fun hacerPreguntaAlAsistente() {
        print("Escriba su pregunta para la IA: ")
        val pregunta = readln()

        // La referencia al campo 'controlador' de la clase es correcta aquí.
        val respuesta = controlador.obtenerRespuestaAsistente(pregunta)

        println("--- Respuesta del Asistente IA ---")
        println(respuesta)
    }
}
