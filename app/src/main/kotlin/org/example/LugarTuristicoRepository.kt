package org.example

//define un conjunto de métodos (funciones) que una clase debe implementar, pero no proporciona la lógica de esos métodos
interface LugarTuristicoRepository {
    fun obtenerTodos(): List<LugarTuristico>
    fun buscarPorTemporada(temporada: Temporada): List<LugarTuristico>
}