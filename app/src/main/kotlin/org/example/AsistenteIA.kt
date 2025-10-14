package org.example

/**
 * Interfaz que define el contrato de comunicación con el servicio de Inteligencia Artificial (IA),
 * que incluye la funcionalidad de chat conversacional y la de enriquecimiento de datos.
 */
interface AsistenteIA {
    /**
     * Función original para obtener una respuesta conversacional.
     * @param pregunta La pregunta de texto del usuario.
     * @return La respuesta de texto de la IA.
     */
    fun obtenerRespuesta(pregunta: String): String

    /**
     * Nueva función para enriquecer la descripción de un lugar turístico.
     * Este método se comunica con el servicio de IA para obtener la versión mejorada.
     * @param nombre El nombre del lugar.
     * @param descripcion La descripción actual a potenciar.
     * @return La descripción enriquecida por la IA.
     */
    fun enriquecerLugarTuristico(nombre: String, descripcion: String): String
}