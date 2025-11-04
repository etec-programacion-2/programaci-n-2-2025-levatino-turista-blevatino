package org.example

/**
 * Orquestador central de la lógica de la aplicación.
 * Gestiona el flujo de trabajo, decide qué servicio llamar (local o IA)
 * y mantiene el estado del historial del chat (para el modo web, este historial
 * es compartido por simplicidad, pero se manejaría por sesión en un entorno real).
 */
class ControladorPrincipal(
    val servicioRecomendaciones: ServicioRecomendaciones, // <-- HECHO PÚBLICO (val)
    val asistenteIA: AsistenteIA // <-- HECHO PÚBLICO (val)
) {

    // --- Estado de la Memoria del Chat (compartido globalmente en este ejemplo) ---
    private val historialChat: MutableList<Mensaje> = mutableListOf()

    /**
     * Responde a una nueva pregunta del usuario usando el asistente de IA,
     * manteniendo la memoria de la conversación.
     *
     * @param nuevaPregunta El String con la última pregunta del usuario.
     * @return La respuesta generada por la IA.
     */
    suspend fun obtenerRespuestaAsistente(nuevaPregunta: String): String {
        // La lógica de manejar el historial se ha movido al frontend en la arquitectura web.
        // Aquí, simplemente delegamos la petición. Si se llama desde las rutas web,
        // la ruta deberá pasar el historial completo, no solo la pregunta.
        // Pero mantenemos esta función por si alguna otra parte la requiere.

        // **NOTA: Para Ktor, la ruta /chat necesita un endpoint que reciba el historial completo (ver ApiRoutes.kt)**

        // Lógica de memoria compartida (Mantener la función original para compatibilidad con la consola si aplica)
        historialChat.add(Mensaje(role = "user", content = nuevaPregunta))
        val respuestaIA = asistenteIA.obtenerRespuesta(historialChat)
        historialChat.add(Mensaje(role = "assistant", content = respuestaIA))
        return respuestaIA
    }

    // --- FUNCIONES DE SERVICIO LOCAL ---\

    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    /**
     * Lógica para enriquecer la descripción de un lugar usando la IA.
     * La modificación del lugar ocurre en el modelo (lugar: LugarTuristico).
     */
    suspend fun enriquecerDescripcionLugar(lugar: LugarTuristico) {
        val descripcionEnriquecida = asistenteIA.enriquecerLugarTuristico(
            lugar.nombre,
            lugar.descripcion
        )
        // La IA devuelve el prefijo. Lo eliminamos para que el modelo quede limpio.
        if (descripcionEnriquecida.startsWith("PotenciadoIA:", true)) {
            lugar.descripcion = descripcionEnriquecida.substringAfter(":")
        } else {
            lugar.descripcion = descripcionEnriquecida // Si no tiene el prefijo, usamos la respuesta completa.
        }
    }
}