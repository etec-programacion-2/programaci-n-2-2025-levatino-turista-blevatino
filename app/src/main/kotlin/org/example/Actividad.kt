package org.example

import kotlinx.serialization.Serializable

// La data class es para almacenar información que puede variar
@Serializable // permite que la librería las convierta automáticamente entre objetos y JSON
data class Actividad (

    // Se declaran los atributos / variables como privadas
    private var _nombre: String,
    private var _descripcion: String

) {

    // Se les da el valor a las variables
    var nombre: String = _nombre
        get() = field
        set(value) { field = value }

    var descripcion: String = _descripcion
        get() = field
        set(value) { field = value }


}