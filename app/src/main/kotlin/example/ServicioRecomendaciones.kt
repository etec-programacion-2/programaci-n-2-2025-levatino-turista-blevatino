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
        return repository.obtenerPorTemporada(temporada)
    }

    /**
     * Obtiene la lista completa de lugares turísticos.
     */
    fun obtenerTodos(): List<LugarTuristico> {
        return repository.obtenerTodos()
    }

    /**
     * Busca un lugar específico por su ID, crucial para las rutas web.
     */
    fun obtenerLugarPorId(id: Int?): LugarTuristico? {
        if (id == null) return null
        // Asumiendo que LugarTuristico tiene la propiedad 'id'
        return repository.obtenerTodos().firstOrNull { it.id == id }
    }
}