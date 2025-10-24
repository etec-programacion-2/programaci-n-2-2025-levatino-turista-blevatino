package org.example

/**
 * Orquestador principal de la aplicación.
 * Recibe todos los servicios y la lógica de negocio, y expone los métodos
 * que la Vista Consola (o cualquier UI) puede llamar.
 * * Cumple con el Principio de Inversión de Dependencias (DIP) al depender de abstracciones (interfaces).
 *
 * @property servicioRecomendaciones Lógica de negocio para obtener lugares turísticos.
 * @property asistenteIA Servicio de la IA para chat y enriquecimiento de texto.
 * @property servicioMeteorologico Servicio para obtener hora y pronóstico de clima.
 */
class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA,
    private val servicioMeteorologico: ServicioMeteorologico
) {

    // --- Métodos de Recomendación de Turismo (Delegación a ServicioRecomendaciones) ---

    /**
     * Obtiene la lista de lugares turísticos recomendados para una temporada específica.
     */
    fun obtenerRecomendacionesPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    /**
     * Obtiene todos los lugares turísticos registrados.
     */
    fun obtenerTodosLosLugares(): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerTodos()
    }

    // --- Métodos de Asistente IA (Delegación a AsistenteIA) ---

    /**
     * Envía un historial de chat a la IA y recibe una respuesta conversacional.
     */
    suspend fun obtenerRespuestaChat(historial_mensajes: List<Mensaje>): String {
        return asistenteIA.obtenerRespuesta(historial_mensajes)
    }

    /**
     * Pide a la IA enriquecer o mejorar la descripción de un lugar turístico.
     */
    suspend fun enriquecerDescripcion(nombre: String, descripcion: String): String {
        return asistenteIA.enriquecerLugarTuristico(nombre, descripcion)
    }

    // --- Métodos de Servicios de Utilidad (Delegación a ServicioMeteorologico) ---

    /**
     * Obtiene la hora actual desde el servicio meteorológico.
     */
    suspend fun obtenerHoraActual(): String {
        return servicioMeteorologico.obtenerHoraActual()
    }

    /**
     * Obtiene el pronóstico del clima a 5 días desde el servicio meteorológico.
     */
    suspend fun obtenerPronosticoClima(): List<PronosticoDia> {
        return servicioMeteorologico.obtenerPronosticoClima()
    }
}


