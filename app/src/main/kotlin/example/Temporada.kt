package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class Temporada(val nombreDisplay: String) { // <--- AÑADIDO: Constructor
    @SerialName("verano")
    VERANO("Verano"),

    @SerialName("otono")
    OTONO("Otoño"), // <--- CORREGIDO: "Otoño" con Ñ mayúscula

    @SerialName("invierno")
    INVIERNO("Invierno"),

    @SerialName("primavera")
    PRIMAVERA("Primavera"),

    @SerialName("todo_el_ano")
    TODO_EL_ANO("Todo el Año") // <--- CORREGIDO: Display también para este
}