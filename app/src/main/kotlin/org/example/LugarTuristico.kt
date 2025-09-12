package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LugarTuristico(
    @SerialName("nombre")
    private val _nombre: String,

    @SerialName("descripcion")
    private val _descripcion: String,

    @SerialName("ubicacion")
    private val _ubicacion: String,

    @SerialName("actividadesSugeridas")
    private val _actividadesSugeridas: List<Actividad>,

    @SerialName("temporadaRecomendada")
    private val _temporadaRecomendada: Temporada
) {
    // Definimos las propiedades públicas con solo el getter
    val nombre: String get() = _nombre

    val descripcion: String get() = _descripcion

    val ubicacion: String get() = _ubicacion

    val actividadesSugeridas: List<Actividad> get() = _actividadesSugeridas

    val temporadaRecomendada: Temporada get() = _temporadaRecomendada
}