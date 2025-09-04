package org.example

import kotlinx.serialization.Serializable

// La enum class es para valores fijos y constantes
@Serializable
enum class Temporada() {
    VERANO,
    OTONO,
    INVIERNO,
    PRIMAVERA,
    TODO_EL_ANO
}