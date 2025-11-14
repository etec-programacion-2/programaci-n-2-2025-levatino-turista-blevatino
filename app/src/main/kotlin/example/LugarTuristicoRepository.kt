package org.example

// Interfaz para definir las operaciones de acceso a datos de Lugares Turísticos.
interface LugarTuristicoRepository {
    fun obtenerTodos(): List<LugarTuristico>
    fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico>
    fun obtenerPorId(id: Int?): LugarTuristico?
}