package org.example

class ServicioRecomendaciones(private val repository: LugarTuristicoRepository) {

    fun obtenerRecomendacionesPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return repository.obtenerPorTemporada(temporada)
    }

    fun obtenerTodos(): List<LugarTuristico> {
        return repository.obtenerTodos()
    }

    // Función auxiliar necesaria para el enriquecimiento por índice (si se usara en el futuro)
    fun obtenerLugarPorIndice(indice: Int): LugarTuristico? {
        val todosLosLugares = repository.obtenerTodos()
        return if (indice >= 0 && indice < todosLosLugares.size) {
            todosLosLugares[indice]
        } else {
            null
        }
    }
}