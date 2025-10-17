package org.example

/**
 * Interfaz que define las operaciones que debe poder realizar el Asistente de IA.
 * Todas las funciones son 'suspend' porque implican una posible operación de red.
 */
interface AsistenteIA {

    /** Función genérica de chat. Debe ser suspend. */
    suspend fun obtenerRespuesta(pregunta: String): String

    /** Función para enriquecer una descripción. Debe ser suspend. */
    suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String
}
