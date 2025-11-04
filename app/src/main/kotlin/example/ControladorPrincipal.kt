package org.example

import java.io.IOException

/**
 * Orquestador central de la lógica de la aplicación.
 * Gestiona el flujo de trabajo, decide qué servicio llamar (local o IA)
 * y mantiene el estado del historial del chat.
 */
class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA
) {

    // --- Estado de la Memoria del Chat ---
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    /**
     * Responde a una nueva pregunta del usuario usando el asistente de IA,
     * manteniendo la memoria de la conversación.
     */
    suspend fun obtenerRespuestaAsistente(nuevaPregunta: String): String {
        // 1. Agregar la pregunta del usuario al historial
        historialChat.add(Mensaje(role = "user", content = nuevaPregunta))

        try {
            // 2. Llamar al asistente enviando TODO el historial.
            val respuestaIA = asistenteIA.obtenerRespuesta(historialChat)

            // 3. Agregar la respuesta de la IA al historial
            historialChat.add(Mensaje(role = "assistant", content = respuestaIA))

            return respuestaIA
        } catch (e: Exception) {
            // Si la llamada falla, se elimina la última pregunta del usuario para no contaminar el historial
            if (historialChat.lastOrNull()?.role == "user") {
                historialChat.removeLast()
            }
            throw e // Relanzar la excepción para que la Vista la maneje
        }
    }

    /**
     * Lógica para obtener la descripción enriquecida y actualizar el lugar en memoria si fue potenciada por IA.
     */
    suspend fun obtenerDescripcionEnriquecidaLugar(lugar: LugarTuristico): String {
        val descripcionEnriquecidaEtiquetada = asistenteIA.enriquecerLugarTuristico(
            lugar.nombre,
            lugar.descripcion
        )

        // El controlador decide si actualiza el modelo basado en la etiqueta de respuesta
        if (descripcionEnriquecidaEtiquetada.startsWith("PotenciadoIA:", true)) {
            // Si fue potenciado por IA, actualiza la descripción del objeto mutable
            // Se usa substringAfter para eliminar el prefijo "PotenciadoIA:"
            lugar.descripcion = descripcionEnriquecidaEtiquetada.substringAfter(":", "").trim()
        }

        // Retorna la cadena etiquetada (ej: "PotenciadoIA: Nueva descripcion" o "BaseDeDatos: Descripcion original")
        return descripcionEnriquecidaEtiquetada
    }

    // --- FUNCIONES DE SERVICIO LOCAL (Delegación) ---

    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }
}