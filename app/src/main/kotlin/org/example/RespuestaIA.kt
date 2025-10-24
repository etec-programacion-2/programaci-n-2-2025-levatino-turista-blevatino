package org.example

import kotlinx.serialization.Serializable

/**
 * Clase de datos para la respuesta general del servidor IA.
 * Incluye campos para la respuesta exitosa o un mensaje de error.
 * ESTA CLASE DEBE COINCIDIR EXACTAMENTE con el JSON que devuelve el servidor Python.
 */
@Serializable
data class RespuestaIA(
    val respuesta: String? = null,
    val error: String? = null
)
