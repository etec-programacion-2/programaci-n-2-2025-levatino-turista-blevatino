package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Representa la estructura de la respuesta JSON que se recibe del servidor Python.
 *
 * El servidor puede devolver la 'respuesta' (descripción enriquecida) o un 'error'.
 * Ambos campos son opcionales (nullable) porque solo uno estará presente a la vez.
 */
@Serializable
data class RespuestaEnriquecimiento(
    @SerialName("respuesta")
    val respuesta: String? = null,

    @SerialName("error")
    val error: String? = null
)