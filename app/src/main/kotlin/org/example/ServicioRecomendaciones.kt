package org.example

class ServicioRecomendaciones(private val repositorio: LugarTuristicoRepository) {
    /**
     * Devuelve una lista de lugares turísticos recomendados para una temporada específica.
     * Utiliza el repositorio para obtener los datos y luego aplica la lógica de negocio.
     */
    fun obtenerRecomendacionesPara(temporada: Temporada): List<LugarTuristico> {
        // La lógica de negocio: usar el repositorio y aplicar el filtro
        return repositorio.buscarPorTemporada(temporada)
    }

    /**
     * Devuelve todos los lugares turísticos, sin aplicar ninguna regla de negocio.
     * Es un método simple que solo delega la tarea al repositorio.
     */
    fun obtenerTodosLosLugares(): List<LugarTuristico> {
        return repositorio.obtenerTodos()

    }
}