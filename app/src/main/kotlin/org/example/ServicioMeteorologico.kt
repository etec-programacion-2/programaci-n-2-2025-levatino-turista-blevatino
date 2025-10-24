package org.example

/**
 * Interfaz que define las operaciones para obtener datos meteorológicos y de tiempo.
 * Esto cumple con el Principio de Responsabilidad Única (SRP), separando datos de la IA.
 */
interface ServicioMeteorologico {

    /**
     * Obtiene la hora actual para Mendoza, Argentina.
     * @return Una cadena formateada que representa la hora actual.
     */
    suspend fun obtenerHoraActual(): String

    /**
     * Obtiene el pronóstico simulado para el clima de Mendoza, incluyendo hoy y 4 días futuros.
     * @return Una lista de objetos PronosticoDia.
     */
    suspend fun obtenerPronosticoClima(): List<PronosticoDia>
}