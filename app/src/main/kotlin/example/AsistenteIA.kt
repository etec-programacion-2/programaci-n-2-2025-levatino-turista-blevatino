package org.example

/**
 * Interfaz que define las operaciones que debe poder realizar el Asistente de IA.
 * Ambas funciones son suspendidas (suspend) porque implican operaciones de red asíncronas.
 */
interface AsistenteIA {

    /**
     * Función genérica para hacer una pregunta a la IA, manteniendo el historial de chat (memoria).
     * @param historial_mensajes La lista completa de Mensajes (incluyendo el último del usuario).
     */
    suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String

    /**
     * Función específica para enriquecer la descripción de un lugar turístico.
     * Retorna la descripción enriquecida (que incluye el prefijo PotenciadoIA: o BaseDeDatos:).
     */
    suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String
}