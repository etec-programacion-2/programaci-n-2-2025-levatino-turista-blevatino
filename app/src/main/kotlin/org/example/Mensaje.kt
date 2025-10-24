package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Mensaje(
    val role: String, // Puede ser "user" o "assistant"
    val content: String // El contenido del texto
)
