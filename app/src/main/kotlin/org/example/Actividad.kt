package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Actividad(
    @SerialName("nombre")
    private val _nombre: String,

    @SerialName("descripcion")
    private val _descripcion: String
) {
    // Definimos las propiedades públicas con solo el getter
    val nombre: String get() = _nombre
    val descripcion: String get() = _descripcion
}