package org.example

class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA
) {
    /**
     * Recibe una petición de la UI para obtener recomendaciones y delega la tarea
     * al ServicioRecomendaciones.
     * @param temporada El nombre de la temporada como String, recibido de la UI.
     * @return Una lista de lugares turísticos recomendados.
     */
    fun solicitarRecomendaciones(temporada: String): List<LugarTuristico> {
        // La lógica de negocio está en otro lugar, el controlador solo delega.
        // Aquí puedes manejar la conversión de String a un Enum (Temporada).
        val temporadaEnum = try {
            Temporada.valueOf(temporada.uppercase())
        } catch (e: IllegalArgumentException) {
            println("Temporada '$temporada' no válida.")
            return emptyList()
        }
        return servicioRecomendaciones.obtenerRecomendacionesPara(temporadaEnum)
    }

    /**
     * Recibe una pregunta de la UI y delega la tarea al AsistenteIA.
     * @param pregunta La pregunta de texto del usuario.
     * @return La respuesta de texto de la IA.
     */
    fun preguntarAlAsistente(pregunta: String): String {
        // La lógica para obtener la respuesta de la IA está en el AsistenteIA.
        return asistenteIA.obtenerRespuesta(pregunta)
    }
}