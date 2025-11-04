package org.example

import java.io.IOException
import java.util.InputMismatchException

/**
 * Vista de la aplicación para la interfaz de consola.
 * Maneja la interacción con el usuario y utiliza los servicios y el asistente de IA.
 */
class VistaConsola(
    private val repositorio: LugarTuristicoRepository,
    private val asistente: AsistenteIA
) {
    // Memoria para el chat, necesaria para las peticiones contextuales.
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    /**
     * Inicia el bucle principal de la aplicación de consola.
     * Es una función suspendida porque el menú incluye llamadas a la IA.
     */
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
                opcion = -1 // Forzar la repetición del bucle
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

        // CORRECCIÓN CLAVE: Usamos nombreDisplay para mostrar "Otoño"
        val temporadasFiltradas = Temporada.entries.filter { it != Temporada.TODO_EL_ANO }

        temporadasFiltradas.forEachIndexed { index, temporada ->
            println("${index + 1}. ${temporada.nombreDisplay}")
        }
        print("Introduce el número de la temporada: ")

        val input = readlnOrNull()?.toIntOrNull()
        val temporadaIndex = input?.minus(1) // Convertimos el número de opción a índice de lista (0-basado)

        val temporadaSeleccionada = if (temporadaIndex != null && temporadaIndex in temporadasFiltradas.indices) {
            // Obtenemos la temporada correcta por índice
            temporadasFiltradas.getOrNull(temporadaIndex)
        } else {
            println("Selección de temporada no válida.")
            return
        }

        if (temporadaSeleccionada != null) {
            val lugares = repositorio.obtenerPorTemporada(temporadaSeleccionada)

            // CORRECCIÓN CLAVE: Usamos nombreDisplay en el encabezado
            println("\n--- Lugares recomendados para ${temporadaSeleccionada.nombreDisplay} ---")

            if (lugares.isEmpty()) {
                println("No se encontraron lugares para esta temporada.")
            } else {
                lugares.forEachIndexed { index, lugar ->
                    println("[$index] ${lugar.nombre} (${lugar.ubicacion})")
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
                    // 1. Agregar la pregunta del usuario al historial
                    historialChat.add(Mensaje(role = "user", content = pregunta))

                    // 2. Llamada a la función suspendida del asistente
                    val respuestaIA = asistente.obtenerRespuesta(historialChat)

                    // 3. La respuesta ya fue agregada al historial dentro del asistente (se asume, aunque en este caso se agrega en el Controlador/Vista)
                    historialChat.add(Mensaje(role = "assistant", content = respuestaIA)) // Se añade la respuesta aquí para asegurar la memoria
                    println("IA: $respuestaIA")

                } catch (e: IOException) {
                    // Manejo de errores de red o parsing
                    println("🔴 Error de comunicación con el asistente: ${e.message}")
                    // Intentamos revertir la adición de la pregunta al historial
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
        val todosLosLugares = repositorio.obtenerTodos()
        if (todosLosLugares.isEmpty()) {
            println("No hay lugares cargados para enriquecer.")
            return
        }

        println("\n--- Seleccionar Lugar a Enriquecer ---")
        todosLosLugares.forEachIndexed { index, lugar ->
            println("[$index] ${lugar.nombre} (${lugar.ubicacion})")
        }
        print("Introduce el ID del lugar para enriquecer (0-${todosLosLugares.size - 1}): ")

        val input = readlnOrNull()?.toIntOrNull()

        if (input != null && input in todosLosLugares.indices) {
            // NOTA: En Kotlin, al obtener un objeto de una lista (como 'lugarSeleccionado'),
            // se obtiene una referencia. Si el objeto (LugarTuristico) es mutable (tiene 'var' en la descripción),
            // la modificación de esa referencia afecta al objeto original en el repositorio.
            val lugarSeleccionado = todosLosLugares[input]

            println("\nOriginal: ${lugarSeleccionado.descripcion.take(100)}...")
            println("Enriqueciendo la descripción de ${lugarSeleccionado.nombre} con IA...")

            try {
                // Llamada suspendida para el enriquecimiento
                val resultado = asistente.enriquecerLugarTuristico(
                    nombre = lugarSeleccionado.nombre,
                    descripcion = lugarSeleccionado.descripcion
                )

                // El resultado debe contener el prefijo de etiquetado
                if (resultado.startsWith("PotenciadoIA:", true)) {
                    // Actualizamos el lugar si la IA devolvió nuevo contenido
                    lugarSeleccionado.descripcion = resultado.substringAfter(":", "").trim()
                    println("✅ Descripción enriquecida con éxito.")
                    println("Nueva Descripción: ${lugarSeleccionado.descripcion.take(150)}...")
                } else {
                    println("La IA no enriqueció la descripción o el formato fue incorrecto.")
                    println("Respuesta cruda: $resultado")
                }

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