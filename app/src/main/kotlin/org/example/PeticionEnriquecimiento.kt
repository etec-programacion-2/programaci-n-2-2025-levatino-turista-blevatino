package org.example
import kotlinx.serialization.Serializable

/**
 * Representa el cuerpo de la petición que se envía al servidor Python.
 * El servidor Flask espera 'lugar_nombre' y 'descripcion_actual'.
 */
@Serializable
data class PeticionEnriquecimiento(
    val lugar_nombre: String,
    val descripcion_actual: String
)