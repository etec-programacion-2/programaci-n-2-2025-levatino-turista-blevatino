package org.example

/**
 * Interfaz que define el contrato de acceso a datos para la entidad LugarTuristico.
 * Esto permite cambiar la fuente de datos (JSON local, base de datos, API)
 * sin modificar la lógica de negocio, lo que facilita las pruebas y el mantenimiento.
 */
interface LugarTuristicoRepository {

    /**
     * Obtiene todos los lugares turísticos del repositorio, sin aplicar filtros.
     * Es necesario para listar los lugares en la consola antes de enriquecerlos.
     */
    fun obtenerTodos(): List<LugarTuristico>

    /**
     * Filtra y obtiene los lugares turísticos recomendados para una temporada específica.
     */
    fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico>

    // Puedes añadir otros métodos aquí (ej. obtenerPorId, guardar, etc.) si es necesario.
}