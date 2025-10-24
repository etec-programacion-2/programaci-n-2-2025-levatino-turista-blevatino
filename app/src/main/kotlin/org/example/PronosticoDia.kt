package org.example

/**
 * Clase de datos que representa el pronóstico para un día específico.
 * Mueve la definición de datos a su propio archivo.
 */
data class PronosticoDia(
    val dia: String,
    val condicion: String,
    val tempMax: String,
    val tempMin: String,
    val esActual: Boolean = false
)