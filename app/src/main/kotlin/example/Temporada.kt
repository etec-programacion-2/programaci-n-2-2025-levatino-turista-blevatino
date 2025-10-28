package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class Temporada {
    @SerialName("verano")
    VERANO,

    @SerialName("otono")
    OTONO,

    @SerialName("invierno")
    INVIERNO,

    @SerialName("primavera")
    PRIMAVERA,

    @SerialName("todo_el_ano") // <-- ¡Este es el valor faltante!
    TODO_EL_ANO
}
