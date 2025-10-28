package org.example

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
    // El controlador principal es el responsable de mantener el historial de la conversación.
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    /**
     * Responde a una nueva pregunta del usuario usando el asistente de IA,
     * manteniendo la memoria de la conversación.
     *
     * Esta función es 'suspend' porque delega la llamada a la red al AsistenteIA.
     *
     * @param nuevaPregunta El String con la última pregunta del usuario.
     * @return La respuesta generada por la IA.
     */
    suspend fun obtenerRespuestaAsistente(nuevaPregunta: String): String {
        // 1. Agregar la pregunta del usuario al historial
        historialChat.add(Mensaje(role = "user", content = nuevaPregunta))

        // 2. Llamar al asistente enviando TODO el historial.
        // ESTO RESUELVE EL ERROR DE COMPILACIÓN: se pasa List<Mensaje> en lugar de String.
        val respuestaIA = asistenteIA.obtenerRespuesta(historialChat)

        // 3. Agregar la respuesta de la IA al historial
        historialChat.add(Mensaje(role = "assistant", content = respuestaIA))

        return respuestaIA
    }

    // --- FUNCIONES DE SERVICIO LOCAL ---

    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    /**
     * Lógica para enriquecer la descripción de un lugar usando la IA.
     */
    suspend fun enriquecerDescripcionLugar(lugar: LugarTuristico) {
        val descripcionEnriquecida = asistenteIA.enriquecerLugarTuristico(
            lugar.nombre,
            lugar.descripcion
        )
        // La lógica de etiquetado (BaseDeDatos:, PotenciadoIA:) debe manejarse en VistaConsola,
        // pero aquí el controlador simplemente actualiza el modelo.
        if (descripcionEnriquecida.startsWith("PotenciadoIA:", true)) {
            lugar.descripcion = descripcionEnriquecida.substringAfter(":")
        }
        // Nota: En una arquitectura MVC pura, el controlador sólo notificaría un cambio.
    }

    // Función de soporte para acceder al historial (si fuera necesario en la UI)
    fun getHistorialChat(): List<Mensaje> = historialChat
}

