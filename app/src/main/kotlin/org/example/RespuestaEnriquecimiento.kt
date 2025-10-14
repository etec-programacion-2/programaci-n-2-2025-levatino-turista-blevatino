package org.example
import kotlinx.serialization.Serializable

/**
 * Representa la respuesta que se recibe del servidor Python.
 * Puede ser la 'respuesta' (descripción enriquecida) o un 'error'.
 */
@Serializable
data class RespuestaEnriquecimiento(
    val respuesta: String? = null,
    val error: String? = null
)