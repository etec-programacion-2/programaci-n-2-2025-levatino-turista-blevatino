package org.example

// Interfaz para la comunicación con el servicio de Inteligencia Artificial.
interface AsistenteIA {

    // Envía un historial de chat para obtener una respuesta contextual.
    suspend fun obtenerRespuesta(historial_mensajes: List<Mensaje>): String

    // Envía un lugar para que la IA enriquezca su descripción.
    suspend fun enriquecerLugarTuristico(nombre: String, descripcion: String): String
}