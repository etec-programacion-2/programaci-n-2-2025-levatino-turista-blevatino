package org.example

import java.io.IOException
import java.util.InputMismatchException

// Vista de la aplicación para la interfaz de consola.
// Delega toda la lógica al ControladorPrincipal.
class VistaConsola(
    private val controlador: ControladorPrincipal
) {
    // Historial para mantener el contexto del chat.
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    // Inicia el bucle principal del menú de la consola.
    suspend fun iniciar() {
        println("=========================================")
        println("   Bienvenido al Asistente Turístico IA  ")
        println("=========================================")

        var opcion: Int? = null

        do {
            mostrarMenu()
            try {
                opcion = readlnOrNull()?.toIntOrNull()

                when (opcion) {
                    1 -> mostrarLugaresPorTemporada()
                    2 -> iniciarChat()
                    3 -> enriquecerLugar()
                    0 -> println("Saliendo del asistente. ¡Adiós!")
                    else -> println("Opción no válida. Inténtalo de nuevo.")
                }
            } catch (e: NumberFormatException) {
                println("Entrada no válida. Por favor, introduce un número.")
                opcion = -1
            } catch (e: Exception) {
                System.err.println("Ocurrió un error inesperado: ${e.message}")
            }
        } while (opcion != 0)
    }

    private fun mostrarMenu() {
        println("\n--- Menú Principal ---")
        println("1. Ver Lugares por Temporada")
        println("2. Iniciar Chat con el Asistente IA")
        println("3. Enriquecer la Descripción de un Lugar (IA)")
        println("0. Salir")
        print("Selecciona una opción: ")
    }

    // --- Opción 1: Mostrar Lugares por Temporada ---

    private fun mostrarLugaresPorTemporada() {
        println("\n--- Seleccionar Temporada ---")

        val temporadasFiltradas = Temporada.entries.filter { it != Temporada.TODO_EL_ANO }

        temporadasFiltradas.forEachIndexed { index, temporada ->
            println("${index + 1}. ${temporada.nombreDisplay}")
        }
        print("Introduce el número de la temporada: ")

        val input = readlnOrNull()?.toIntOrNull()
        val temporadaIndex = input?.minus(1)

        val temporadaSeleccionada = if (temporadaIndex != null && temporadaIndex in temporadasFiltradas.indices) {
            temporadasFiltradas.getOrNull(temporadaIndex)
        } else {
            println("Selección de temporada no válida.")
            return
        }

        if (temporadaSeleccionada != null) {
            // Delega la obtención de recomendaciones al controlador
            val lugares = controlador.solicitarRecomendaciones(temporadaSeleccionada)

            println("\n--- Lugares recomendados para ${temporadaSeleccionada.nombreDisplay} ---")

            if (lugares.isEmpty()) {
                println("No se encontraron lugares para esta temporada.")
            } else {
                lugares.forEachIndexed { index, lugar ->
                    println("[ID: ${lugar.id}] ${lugar.nombre} (${lugar.ubicacion})")
                    println("    Descripción: ${lugar.descripcion.take(100)}...")
                    println("    Actividades: ${lugar.actividades.joinToString { it.nombre }}")
                    println("---------------------------------")
                }
            }
        }
    }

    // --- Opción 2: Chat con el Asistente IA ---

    private suspend fun iniciarChat() {
        println("\n--- Chat con Asistente IA (Escribe 'salir' para terminar) ---")

        var pregunta: String
        do {
            print("Tú: ")
            pregunta = readlnOrNull() ?: ""

            if (pregunta.lowercase() == "salir") break

            if (pregunta.isNotBlank()) {
                try {
                    historialChat.add(Mensaje(role = "user", content = pregunta))

                    // Llama al controlador para obtener la respuesta del chat
                    val respuestaIA = controlador.solicitarRespuestaChat(historialChat)

                    historialChat.add(Mensaje(role = "assistant", content = respuestaIA))
                    println("IA: $respuestaIA")

                } catch (e: IOException) {
                    println("🔴 Error de comunicación con el asistente: ${e.message}")
                    // Elimina el mensaje del usuario si la IA falla
                    if (historialChat.lastOrNull()?.content == pregunta) {
                        historialChat.removeLast()
                    }
                } catch (e: Exception) {
                    println("🔴 Error: ${e.message}")
                }
            }
        } while (true)
    }

    // --- Opción 3: Enriquecer Descripción (IA) ---

    private suspend fun enriquecerLugar() {
        // Obtiene los datos a través del servicio en el controlador.
        val todosLosLugares = controlador.servicioRecomendaciones.obtenerTodos().associateBy { it.id }

        if (todosLosLugares.isEmpty()) {
            println("No hay lugares cargados para enriquecer.")
            return
        }

        println("\n--- Seleccionar Lugar a Enriquecer ---")
        // Muestra la lista de IDs disponibles
        todosLosLugares.values.forEach { lugar ->
            println("[ID: ${lugar.id}] ${lugar.nombre} (${lugar.ubicacion})")
        }
        print("Introduce el ID del lugar para enriquecer: ")

        val input = readlnOrNull()?.toIntOrNull()

        val lugarSeleccionado = todosLosLugares[input]

        if (lugarSeleccionado != null) {
            println("\nOriginal: ${lugarSeleccionado.descripcion.take(100)}...")
            println("Enriqueciendo la descripción de ${lugarSeleccionado.nombre} con IA...")

            try {
                // Llama al controlador para gestionar el enriquecimiento
                val lugarModificado = controlador.enriquecerDescripcionLugar(lugarSeleccionado)

                println("✅ Descripción enriquecida con éxito.")
                println("Nueva Descripción: ${lugarModificado.descripcion.take(150)}...")

            } catch (e: IOException) {
                println("🔴 Error de comunicación con la IA: ${e.message}")
            } catch (e: Exception) {
                println("🔴 Error inesperado durante el enriquecimiento: ${e.message}")
            }
        } else {
            println("ID de lugar no válido.")
        }
    }
}