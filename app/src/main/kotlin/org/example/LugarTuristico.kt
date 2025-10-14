package org.example
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LugarTuristico(

    @SerialName("nombre")
    private val _nombre: String,

    @SerialName("descripcion")
    private var _descripcion: String, // Cambiado a 'var' para permitir la modificación después de la inicialización

    @SerialName("ubicacion")
    private val _ubicacion: String,

    @SerialName("actividadesSugeridas")
    private val _actividadesSugeridas: List<Actividad>,

    @SerialName("temporadaRecomendada")
    private val _temporadaRecomendada: Temporada
) {
    val nombre: String get() = _nombre

    // Ahora permite la lectura y la ESCRITURA (mutabilidad) de la descripción
    var descripcion: String
        get() = _descripcion
        set(value) { _descripcion = value }

    val ubicacion: String get() = _ubicacion
    val actividadesSugeridas: List<Actividad> get() = _actividadesSugeridas
    val temporadaRecomendada: Temporada get() = _temporadaRecomendada
}
