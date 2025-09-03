package org.example

data class LugarTuristico (

    // Se declaran los atributos / variables como privadas
    private var _nombre: String,
    private var _ubicacion: String,
    private var _descripcion: String

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



}