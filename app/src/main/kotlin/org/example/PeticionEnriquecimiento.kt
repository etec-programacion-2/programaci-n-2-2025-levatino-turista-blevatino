package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Representa el cuerpo de la petición JSON que se envía al servidor Python
 * para solicitar el enriquecimiento de la descripción.
 *
 * Utiliza @Serializable para que Ktor sepa cómo convertir esta clase a JSON.
 */
@Serializable
data class PeticionEnriquecimiento(
    @SerialName("lugar_nombre")
    val lugar_nombre: String,

    @SerialName("descripcion_actual")
    val descripcion_actual: String
)