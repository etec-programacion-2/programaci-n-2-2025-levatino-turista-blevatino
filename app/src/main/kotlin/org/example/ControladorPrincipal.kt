package org.example

class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA
) {
    /**
     * Delega la solicitud de recomendaciones de lugares al servicio de negocio.
     * Ahora espera el objeto Temporada (enum) directamente, ya que la UI se encarga
     * de validar la entrada String.
     * * @param temporada El objeto Temporada (enum) a buscar.
     */
    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        // Llama al servicio con el Enum de Temporada.
        return servicioRecomendaciones.obtenerRecomendacionesPara(temporada)
    }

    /**
     * Envía una pregunta de texto al asistente de IA y retorna su respuesta.
     * Llama al método de la interfaz AsistenteIA.
     * @param pregunta La pregunta del usuario.
     */
    fun obtenerRespuestaAsistente(pregunta: String): String {
        return asistenteIA.obtenerRespuesta(pregunta)
    }
}
