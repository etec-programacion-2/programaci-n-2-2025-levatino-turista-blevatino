package org.example

interface LugarTuristicoRepository {
    fun obtenerTodos(): List<LugarTuristico>
    fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico>
    // [NUEVO] Agregado para búsqueda directa por ID (eficiencia)
    fun obtenerPorId(id: Int?): LugarTuristico?
}