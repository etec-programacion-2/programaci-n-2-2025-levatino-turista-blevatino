package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class Temporada(val nombreDisplay: String) {
    @SerialName("verano")
    VERANO("Verano"),

    @SerialName("otono")
    OTONO("Otoño"),

    @SerialName("invierno")
    INVIERNO("Invierno"),

    @SerialName("primavera")
    PRIMAVERA("Primavera"),

    @SerialName("todo_el_ano")
    TODO_EL_ANO("Todo el Año")
}