package org.example

/**
 * Servicio de Negocio.
 * Contiene la lógica para obtener y filtrar lugares turísticos.
 * Depende del LugarTuristicoRepository para obtener los datos.
 */
class ServicioRecomendaciones(private val repository: LugarTuristicoRepository) {

    /**
     * [FUNCIÓN REQUERIDA] Obtiene recomendaciones de lugares turísticos filtrados por la temporada.
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
     * Busca un lugar específico por su ID.
     * [OPTIMIZADO] Llama al nuevo método 'obtenerPorId' del repositorio.
     */
    fun obtenerLugarPorId(id: Int?): LugarTuristico? {
        if (id == null) return null
        // Asume que obtienesPorId está implementado en el Repository
        return repository.obtenerPorId(id)
    }
}