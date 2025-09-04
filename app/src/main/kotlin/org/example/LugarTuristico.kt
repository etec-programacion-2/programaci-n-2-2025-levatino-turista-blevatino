package org.example

import kotlinx.serialization.Serializable

@Serializable
data class LugarTuristico (

    // Se declaran los atributos / variables como privadas
    private var _nombre: String,
    private var _ubicacion: String,
    private var _descripcion: String,
    private var _actividadesSugeridas: List<Actividad>,
    private var _temporadaRecomendada: Temporada


) {

    // Se les da el valor a las variables
    var nombre: String = _nombre
        get() = field
        set(value) { field = value }

    var ubicacion: String = _ubicacion
        get() = field
        set(value) { field = value }

    var descripcion: String = _descripcion
        get() = field
        set(value) { field = value }

    var actividadesSugeridas: List<Actividad> = _actividadesSugeridas
        get() = field
        set(value) { field = value }

    var temporadaRecomendada: Temporada = _temporadaRecomendada
        get() = field
        set(value) { field = value }




}