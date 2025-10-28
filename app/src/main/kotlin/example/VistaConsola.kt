package org.example

import kotlin.system.exitProcess
import java.io.IOException

class VistaConsola(private val repositorio: LugarTuristicoRepository, private val asistente: AsistenteIA) {

    // --- Memoria del Chat ---
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    suspend fun iniciar() {
        println("--- Bienvenido al Asistente Turístico IA ---")
        var opcion: Int
        do {
            mostrarMenu()
            opcion = leerOpcion()
            manejarOpcion(opcion)
        } while (opcion != 0)
    }

    private fun mostrarMenu() {
        println("\n==============================================")
        println("== Sistema de Información Turística (Beta) ==")
        println("==============================================")
        println("1. Mostrar todos los Lugares Turísticos")
        println("2. Chatear con Asistente IA (Modo Memoria)")
        println("3. Enriquecer una descripción con IA")
        println("4. Mostrar Lugares por Temporada")
        println("0. Salir")
        print("Seleccione una opción: ")
    }

    private fun leerOpcion(): Int {
        return try {
            readlnOrNull()?.toIntOrNull() ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    private suspend fun manejarOpcion(opcion: Int) {
        when (opcion) {
            1 -> mostrarTodosLosLugares()
            2 -> chatearConIA() // NUEVA LÓGICA CON MEMORIA
            3 -> seleccionarLugarParaEnriquecer()
            4 -> mostrarLugaresPorTemporada()
            0 -> {
                println("Gracias por usar el Asistente Turístico. ¡Adiós!")
                exitProcess(0)
            }
            else -> println("Opción inválida. Intente de nuevo.")
        }
    }

    // ------------------------------------------------------------------
    // OP. 2: Lógica de Chat con Historial (Memoria)
    // ------------------------------------------------------------------
    private suspend fun chatearConIA() {
        println("\n--- MODO CHAT CON ASISTENTE IA (TEMAS TURÍSTICOS) ---")
        println("Tu historial actual tiene ${historialChat.size} mensajes. (Escribe 'salir' para volver al menú)")

        // Muestra el historial si no está vacío
        historialChat.forEach { mensaje ->
            println("${if (mensaje.role == "user") "Tú" else "Asistente"}: ${mensaje.content}")
        }

        while (true) {
            print("\nTú: ")
            val pregunta = readlnOrNull()
            if (pregunta.isNullOrBlank() || pregunta.lowercase() == "salir") {
                println("Saliendo del chat. Volviendo al menú principal.")
                break
            }

            // 1. Agregar pregunta del usuario al historial
            historialChat.add(Mensaje(role = "user", content = pregunta))

            try {
                // 2. Llamar al asistente enviando TODO el historial
                val respuestaIA = asistente.obtenerRespuesta(historialChat)

                // 3. Imprimir respuesta
                println("\nAsistente: $respuestaIA")

                // 4. Agregar respuesta de la IA al historial
                historialChat.add(Mensaje(role = "assistant", content = respuestaIA))

            } catch (e: IOException) {
                println("ERROR: Fallo de conexión o comunicación con el servidor de IA: ${e.message}")
                // Si falla, removemos el último mensaje del usuario para no contaminar
                historialChat.removeLastOrNull()
            } catch (e: Exception) {
                println("ERROR inesperado al procesar la respuesta de la IA: ${e.message}")
                historialChat.removeLastOrNull()
            }
        }
    }

    // ------------------------------------------------------------------
    // OP. 3: Enriquecimiento de Lugares
    // ------------------------------------------------------------------
    private suspend fun seleccionarLugarParaEnriquecer() {
        val lugares = repositorio.obtenerTodos()
        if (lugares.isEmpty()) {
            println("No hay lugares turísticos disponibles para enriquecer.")
            return
        }

        println("\n--- ENRIQUECIMIENTO DE DESCRIPCIONES ---")
        lugares.forEachIndexed { index: Int, lugar: LugarTuristico ->
            println("${index + 1}. ${lugar.nombre} (${lugar.ubicacion})")
        }
        print("Seleccione el número del lugar a enriquecer (o 0 para cancelar): ")

        val seleccion = readlnOrNull()?.toIntOrNull()
        if (seleccion == null || seleccion <= 0 || seleccion > lugares.size) {
            println("Selección inválida o cancelada.")
            return
        }

        val lugarSeleccionado = lugares[seleccion - 1]
        println("\nEnriqueciendo la descripción de: ${lugarSeleccionado.nombre}...")
        println("Descripción original: ${lugarSeleccionado.descripcion}")

        try {
            val nuevaDescripcionCompleta = asistente.enriquecerLugarTuristico(
                lugarSeleccionado.nombre,
                lugarSeleccionado.descripcion
            )

            // Procesamiento de la etiqueta de respuesta
            if (nuevaDescripcionCompleta.startsWith("BaseDeDatos:", true)) {
                val descripcionBD = nuevaDescripcionCompleta.substringAfter(":")
                println("\n[ETIQUETA: BaseDeDatos] La descripción es excelente y no fue modificada.")
                println("Descripción final: $descripcionBD")
            } else if (nuevaDescripcionCompleta.startsWith("PotenciadoIA:", true)) {
                val descripcionIA = nuevaDescripcionCompleta.substringAfter(":")
                println("\n[ETIQUETA: PotenciadoIA] La descripción ha sido mejorada por la IA.")
                println("Descripción enriquecida: $descripcionIA")
            } else {
                // Si la IA no sigue la instrucción de etiquetado
                println("\n[ERROR ETIQUETADO] Respuesta inesperada del asistente (sin prefijo).")
                println("Respuesta cruda: $nuevaDescripcionCompleta")
            }
        } catch (e: IOException) {
            println("ERROR de conexión: ${e.message}")
        } catch (e: Exception) {
            println("ERROR inesperado al enriquecer: ${e.message}")
        }
    }

    // ------------------------------------------------------------------
    // Opciones Simples
    // ------------------------------------------------------------------

    private fun mostrarTodosLosLugares() {
        val lugares = repositorio.obtenerTodos()
        println("\n--- LISTA COMPLETA DE LUGARES TURÍSTICOS ---")
        if (lugares.isEmpty()) {
            println("No hay lugares cargados.")
            return
        }
        lugares.forEach { lugar ->
            println("- ${lugar.nombre} (${lugar.ubicacion})")
            println("  Descripción: ${lugar.descripcion}")
            println("  Actividades: ${lugar.actividades.joinToString { it.nombre }}")
            println("  Temporada: ${lugar.temporada.name}")
            println("----------------------------------------------")
        }
    }

    private fun mostrarLugaresPorTemporada() {
        println("\n--- FILTRAR LUGARES POR TEMPORADA ---")
        Temporada.entries.filter { it != Temporada.TODO_EL_ANO }.forEachIndexed { index, temp ->
            println("${index + 1}. ${temp.name}")
        }
        println("${Temporada.entries.size}. ${Temporada.TODO_EL_ANO.name} (Todos)")
        print("Seleccione la temporada (número): ")

        val seleccion = readlnOrNull()?.toIntOrNull()
        val temporadaSeleccionada = when (seleccion) {
            1 -> Temporada.VERANO
            2 -> Temporada.OTONO
            3 -> Temporada.INVIERNO
            4 -> Temporada.PRIMAVERA
            5 -> Temporada.TODO_EL_ANO
            else -> {
                println("Selección inválida. Cancelando.")
                return
            }
        }

        val lugaresFiltrados = repositorio.obtenerPorTemporada(temporadaSeleccionada)

        println("\n--- Lugares recomendados para ${temporadaSeleccionada.name} ---")
        if (lugaresFiltrados.isEmpty()) {
            println("No se encontraron lugares para esa temporada.")
        } else {
            lugaresFiltrados.forEach { lugar ->
                println("- ${lugar.nombre} (${lugar.ubicacion})")
            }
        }
    }
}

