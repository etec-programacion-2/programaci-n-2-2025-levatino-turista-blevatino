package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Representa una actividad específica que se puede realizar en un lugar turístico.
 *
 * Utilizamos campos privados para almacenar los datos
 * y propiedades públicas de solo lectura (getters) para exponerlos.
 */
@Serializable
data class Actividad(
    @SerialName("nombre")
    private val _nombre: String,

    @SerialName("descripcion")
    private val _descripcion: String
) {
    // Propiedades públicas de solo lectura
    val nombre: String
        get() = _nombre

    val descripcion: String
        get() = _descripcion
}