package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LugarTuristico(
    @SerialName("id")
    private val _id: Int, // <-- CAMPO 'ID' REQUERIDO AÑADIDO

    @SerialName("nombre")
    private val _nombre: String,

    @SerialName("ubicacion")
    private val _ubicacion: String,

    @SerialName("descripcion")
    private var _descripcion: String, // ¡MUTABLE para el enriquecimiento IA!

    @SerialName("temporada")
    private val _temporada: Temporada,

    @SerialName("actividades")
    private val _actividades: List<Actividad>
) {
    // Propiedades de solo lectura
    val id: Int get() = _id // <-- NUEVO GETTER
    val nombre: String get() = _nombre
    val ubicacion: String get() = _ubicacion
    val temporada: Temporada get() = _temporada
    val actividades: List<Actividad> get() = _actividades

    // Propiedad con getter/setter para mutar la descripción
    var descripcion: String
        get() = _descripcion
        set(value) { _descripcion = value }
}

