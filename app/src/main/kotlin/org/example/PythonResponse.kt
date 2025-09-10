package org.example

import kotlinx.serialization.Serializable

@Serializable
data class PythonResponse(
    val respuesta: String? = null,
    val error: String? = null
)