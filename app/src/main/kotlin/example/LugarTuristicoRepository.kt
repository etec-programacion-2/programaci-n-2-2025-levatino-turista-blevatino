package org.example

interface LugarTuristicoRepository {
    fun obtenerTodos(): List<LugarTuristico>
    fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico>
}