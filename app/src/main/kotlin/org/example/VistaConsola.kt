package org.example

import kotlinx.coroutines.*
import kotlin.system.exitProcess

/**
 * Representa la interfaz de usuario basada en consola.
 * Solo depende del ControladorPrincipal y se encarga de la I/O.
 */
class VistaConsola(
    private val controlador: ControladorPrincipal
) {
    // Variable para almacenar el historial de la conversación (memoria del chat)
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    fun mostrarBienvenida() {
        println("=================================================================")
        println("       Bienvenido al Asistente de Viajes a Mendoza - Cuyo")
        println("=================================================================")
    }

    suspend fun iniciar() {
        mostrarBienvenida()

        while (true) {
            mostrarMenu()
            val opcion = readlnOrNull()?.trim()?.uppercase() ?: ""

            when (opcion) {
                "1" -> solicitarRecomendaciones()
                "2" -> iniciarChat()
                "3" -> mostrarHoraActual()
                "4" -> mostrarPronosticoClima()
                "S" -> {
                    println("\nSaliendo del Asistente de Viajes. ¡Hasta pronto!")
                    exitProcess(0)
                }
                else -> println("\nOpción no válida. Inténtelo de nuevo.")
            }
        }
    }

    private fun mostrarMenu() {
        println("\n--- Menú Principal ---")
        println("1. Obtener recomendaciones por temporada")
        println("2. Chatear con el Asistente de IA (Chat con memoria)")
        println("3. Ver Hora Actual de Mendoza 🕒 (Real)")
        println("4. Ver Pronóstico del Clima de Mendoza (5 días) 🌤️ (Simulado)")
        println("S. Salir")
        print("Seleccione una opción: ")
    }

    private suspend fun solicitarRecomendaciones() {
        println("\n--- Recomendaciones por Temporada ---")
        val temporada = solicitarTemporada() ?: return

        val lugares = controlador.obtenerRecomendacionesPorTemporada(temporada)

        if (lugares.isEmpty()) {
            println("\nActualmente no hay lugares registrados para la temporada '$temporada'.")
            return
        }

        println("\n✅ Lugares recomendados para la temporada '$temporada':")
        lugares.forEachIndexed { index, lugar ->
            // El índice aquí es relativo a la lista filtrada, no a la lista completa del repositorio.
            println("${index + 1}. ${lugar.nombre}")
        }

        // --- Opción de enriquecer la descripción (Respuesta a tu última solicitud) ---
        print("\n¿Desea ver una descripción enriquecida por IA de alguno de estos lugares? (Escriba el número o 'N' para omitir): ")
        val seleccion = readlnOrNull()?.trim()

        if (seleccion.isNullOrBlank() || seleccion.uppercase() == "N") return

        val indiceUI = seleccion.toIntOrNull()?.minus(1)
        if (indiceUI != null && indiceUI in lugares.indices) {
            val lugarSeleccionado = lugares[indiceUI]
            mostrarDescripcionEnriquecida(lugarSeleccionado)
        } else {
            println("Selección inválida.")
        }
    }

    private suspend fun mostrarDescripcionEnriquecida(lugar: LugarTuristico) {
        println("\n[Procesando descripción enriquecida con IA...]")
        // Llama al método del controlador, que a su vez llama al AsistenteIA
        val respuesta = controlador.enriquecerDescripcion(lugar.nombre, lugar.descripcion)

        if (respuesta.startsWith("Error") || respuesta.contains("Fallo en la comunicación")) {
            println("ERROR: No se pudo enriquecer la descripción. Asegúrese que el servidor Python esté activo.")
            println("Detalles: $respuesta")
        } else {
            println("--- Descripción de ${lugar.nombre} Potenciada por IA ---")
            println(respuesta)
            println("-----------------------------------------------------")
        }
    }

    private suspend fun iniciarChat() {
        println("\n--- Chat con el Asistente IA ---")
        println("El asistente solo habla de turismo en Mendoza. (Escriba 'FIN' para volver al menú)")

        while (true) {
            print("Tú: ")
            val pregunta = readlnOrNull()?.trim()

            if (pregunta.isNullOrBlank()) continue
            if (pregunta.uppercase() == "FIN") {
                println("Saliendo del chat. El historial de conversación se mantiene.")
                break
            }

            // 1. Añadir pregunta del usuario al historial
            historialChat.add(Mensaje("user", pregunta))

            println("[Procesando con IA...]")

            // 2. Obtener respuesta de la IA a través del controlador
            val respuestaIA = controlador.obtenerRespuestaChat(historialChat)

            // 3. Imprimir respuesta y añadirla al historial (si no es un error)
            if (respuestaIA.startsWith("Error") || respuestaIA.contains("Fallo en la comunicación")) {
                println("Asistente: [ERROR] $respuestaIA")
                historialChat.removeLast() // Remover el último mensaje del usuario para reintentar
            } else {
                println("Asistente: $respuestaIA")
                historialChat.add(Mensaje("assistant", respuestaIA))
            }
        }
    }

    private fun solicitarTemporada(): Temporada? {
        print("Ingrese la temporada (Primavera, Verano, Otono, Invierno): ")
        val entrada = readlnOrNull()?.trim()?.uppercase() ?: ""
        return try {
            Temporada.valueOf(entrada)
        } catch (e: IllegalArgumentException) {
            println("Temporada no reconocida.")
            null
        }
    }

    private suspend fun mostrarHoraActual() {
        println("\nConsultando la hora actual real en Mendoza...")
        val hora = controlador.obtenerHoraActual()
        println("🕒 Hora Actual REAL en Mendoza: $hora")
    }

    private suspend fun mostrarPronosticoClima() {
        println("\nConsultando el pronóstico del clima simulado para Mendoza...")
        val pronostico = controlador.obtenerPronosticoClima()

        if (pronostico.isNotEmpty()) {
            println("\n--- ☀️ Pronóstico para Mendoza, Argentina (5 días) ---")
            pronostico.forEach { dia ->
                val tipoDia = if (dia.esActual) "HOY" else "Día"
                println("[$tipoDia ${dia.dia}]: ${dia.condicion}. Máx: ${dia.tempMax} / Mín: ${dia.tempMin}")
            }
            println("-----------------------------------------------------")
        } else {
            println("Error: No se pudo obtener el pronóstico del clima.")
        }
    }
}


