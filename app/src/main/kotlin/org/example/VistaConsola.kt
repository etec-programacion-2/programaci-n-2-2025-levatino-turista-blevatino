package org.example

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import java.util.Scanner

class VistaConsola(private val controlador: ControladorPrincipal) {

    private val scanner = Scanner(System.`in`)

    fun iniciar() {
        while (true) {
            mostrarMenu()
            when (val opcion = try { scanner.nextInt() } catch (e: Exception) { 0 }) {
                1 -> solicitarRecomendaciones()
                2 -> runBlocking(Dispatchers.IO) { solicitarAsistenteIA() } // runBlocking para llamada suspend
                3 -> runBlocking(Dispatchers.IO) { seleccionarLugarParaEnriquecer() } // runBlocking para llamada suspend
                4 -> {
                    println("¡Gracias por usar el Asistente Turístico! Adiós.")
                    break
                }
                else -> {
                    println("Opción no válida. Inténtelo de nuevo.")
                    scanner.nextLine() // Limpiar buffer en caso de error de formato
                }
            }
        }
    }

    private fun mostrarMenu() {
        println("\n--- Menú Principal ---")
        println("1. Ver lugares por temporada")
        println("2. Preguntar a la IA (Chat Genérico)")
        println("3. 🌟 Enriquecer descripción de un lugar (IA)")
        println("4. Salir")
        print("Seleccione una opción: ")
    }

    private fun solicitarRecomendaciones() {
        scanner.nextLine() // Consumir el salto de línea pendiente
        println("\n--- Seleccione una Temporada ---")
        Temporada.entries.forEachIndexed { index, t ->
            println("${index + 1}. ${t.name}")
        }
        print("Ingrese el número de la temporada: ")

        val seleccion = try {
            scanner.nextInt()
        } catch (e: Exception) {
            println("Entrada inválida. Operación cancelada.")
            scanner.nextLine()
            return
        }

        val temporadaIndex = seleccion - 1
        if (temporadaIndex in Temporada.entries.indices) {
            val temporadaSeleccionada = Temporada.entries[temporadaIndex]
            val lugares = controlador.solicitarRecomendaciones(temporadaSeleccionada)

            println("\n--- Lugares recomendados para ${temporadaSeleccionada.name} ---")
            if (lugares.isEmpty()) {
                println("No se encontraron lugares.")
            } else {
                lugares.forEach { lugar ->
                    println("  * ${lugar.nombre} (${lugar.ubicacion})")
                    println("    Descripción: ${lugar.descripcion}")
                }
            }
        } else {
            println("Selección de temporada no válida.")
        }
    }

    private suspend fun solicitarAsistenteIA() {
        scanner.nextLine() // Consumir el salto de línea pendiente
        print("\nIngrese su pregunta para la IA: ")
        val pregunta = scanner.nextLine()

        try {
            val respuesta = controlador.obtenerRespuestaAsistente(pregunta)

            println("--- Respuesta del Asistente IA ---")
            println(respuesta)
        } catch (e: Exception) {
            println("❌ ERROR: No se pudo obtener la respuesta de la IA. Mensaje: ${e.message}")
        }
    }

    private suspend fun seleccionarLugarParaEnriquecer() {
        val lugares = controlador.solicitarTodosLosLugares()

        if (lugares.isEmpty()) {
            println("No hay lugares turísticos cargados para enriquecer.")
            return
        }

        println("\n--- Seleccione el Lugar a Enriquecer ---")
        // CORRECCIÓN: Se especifican los tipos para resolver el error de inferencia.
        lugares.forEachIndexed { index: Int, lugar: LugarTuristico ->
            println("${index + 1}. ${lugar.nombre} (Actual: ${lugar.descripcion.take(50)}...)")
        }
        print("Ingrese el número del lugar: ")

        val indiceSeleccionado = try {
            scanner.nextInt() - 1 // Restamos 1 para obtener el índice de la lista
        } catch (e: Exception) {
            println("Entrada inválida. Operación cancelada.")
            scanner.nextLine()
            return
        }

        if (indiceSeleccionado in lugares.indices) {
            println("Iniciando enriquecimiento... Esto puede tomar unos segundos.")

            val resultado = controlador.enriquecerDescripcionLugar(indiceSeleccionado)

            println("\n--- Resultado ---")
            println(resultado)

            if (resultado.startsWith("Descripción")) {
                println("\nNueva Descripción:")
                println(lugares[indiceSeleccionado].descripcion)
            }
        } else {
            println("Selección de lugar no válida.")
        }
    }
}

