package org.example

class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA // Ahora implementa la interfaz AsistenteIA
) {
    /**
     * Delega la solicitud de recomendaciones de lugares al servicio de negocio.
     * Ahora espera el objeto Temporada (enum) directamente, ya que la UI se encarga
     * de validar la entrada String.
     * * @param temporada El objeto Temporada (enum) a buscar.
     */
    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        // CORRECCIÓN: Usamos 'obtenerRecomendacionesPara' para asegurar consistencia con el servicio.
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

    /**
     * Usa el AsistenteIA para obtener una descripción enriquecida para un lugar turístico
     * y actualiza el objeto LugarTuristico con la nueva descripción.
     * Este método refleja la nueva funcionalidad de potenciar el contenido.
     *
     * @param lugar El objeto LugarTuristico cuya descripción debe ser mejorada.
     */
    fun enriquecerDescripcionLugar(lugar: LugarTuristico) {
        // 1. Obtener la descripción enriquecida de la IA
        // Se llama al nuevo método del contrato AsistenteIA
        val nuevaDescripcion = asistenteIA.enriquecerLugarTuristico(lugar.nombre, lugar.descripcion)

        // 2. Actualizar el objeto LugarTuristico
        lugar.descripcion = nuevaDescripcion
    }
}

