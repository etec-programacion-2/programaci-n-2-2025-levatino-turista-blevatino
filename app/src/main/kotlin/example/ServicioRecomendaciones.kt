package org.example

// Servicio de Negocio.
// Contiene la lógica para filtrar lugares.
class ServicioRecomendaciones(private val repository: LugarTuristicoRepository) {

    // Obtiene recomendaciones filtradas por la temporada.
    fun obtenerRecomendacionesPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return repository.obtenerPorTemporada(temporada)
    }

    // Obtiene la lista completa de lugares.
    fun obtenerTodos(): List<LugarTuristico> {
        return repository.obtenerTodos()
    }

    // Busca un lugar específico por su ID.
    fun obtenerLugarPorId(id: Int?): LugarTuristico? {
        if (id == null) return null
        return repository.obtenerPorId(id)
    }
}