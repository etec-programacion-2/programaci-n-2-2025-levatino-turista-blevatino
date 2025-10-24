package org.example

import kotlinx.serialization.Serializable

/**
 * Clase de datos de petición específica para el CHAT.
 * Se utiliza para envolver la lista de Mensajes y evitar el error de serialización de ArrayList.
 */
@Serializable
data class PeticionChat(
    val historial_mensajes: List<Mensaje>
)