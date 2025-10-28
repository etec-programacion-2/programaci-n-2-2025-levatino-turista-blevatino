package org.example

/**
 * Interfaz que define las operaciones que debe poder realizar el Asistente de IA.
 * Esto permite el desacoplamiento: el controlador no sabe (ni le importa) si
 * la implementación es real (llamada HTTP) o simulada (para pruebas).
 */
interface AsistenteIA {

    /**
     * Función genérica para hacer una pregunta a la IA, manteniendo el historial de chat (memoria).
     * Debe ser 'suspend' porque implica una operación de red asíncrona.
     * @param historial_mensajes La lista completa de Mensajes (incluyendo el último del usuario).
     */
    suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String // <--- ¡CORREGIDO!

    /**
     * Función específica para enriquecer la descripción de un lugar turístico.
     * Esta función debe ser suspendida porque implica una operación de red asíncrona.
     */
    suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String
}