package org.example

/**
 * Servicio de Negocio.
 * Contiene la lógica para obtener y filtrar lugares turísticos.
 * Depende del LugarTuristicoRepository para obtener los datos.
 */
class ServicioRecomendaciones(private val repository: LugarTuristicoRepository) {

    /**
     * Obtiene recomendaciones de lugares turísticos filtrados por la temporada.
     */
    fun obtenerRecomendacionesPorTemporada(temporada: Temporada): List<LugarTuristico> {
        // En un caso real, aquí podría ir lógica de negocio adicional antes de filtrar.
        return repository.obtenerPorTemporada(temporada)
    }

    /**
     * Obtiene la lista completa de lugares turísticos.
     * Es crucial para que la VistaConsola pueda listar los lugares antes de enriquecerlos.
     */
    fun obtenerTodos(): List<LugarTuristico> {
        return repository.obtenerTodos()
    }

    /**
     * Busca un lugar específico por su índice en la lista completa de lugares.
     */
    fun obtenerLugarPorIndice(indice: Int): LugarTuristico? {
        val todosLosLugares = repository.obtenerTodos()
        return if (indice >= 0 && indice < todosLosLugares.size) {
            todosLosLugares[indice]
        } else {
            null
        }
    }
}
