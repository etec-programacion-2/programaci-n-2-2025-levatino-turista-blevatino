package org.example

// Clase base abstracta para repositorios de Lugares Turísticos.
// Implementa las operaciones comunes de la interfaz.
abstract class BaseLugarTuristicoRepository : LugarTuristicoRepository {

    // Propiedad que almacena los lugares turísticos, cargados al inicializar.
    protected val lugares: List<LugarTuristico> = loadDataFromJson()

    // Método que cada repositorio debe implementar para obtener los datos de su fuente.
    protected abstract fun loadDataFromJson(): List<LugarTuristico>

    // Implementaciones de la interfaz
    override fun obtenerTodos(): List<LugarTuristico> {
        return lugares
    }

    override fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return lugares.filter { it.temporada == temporada || it.temporada == Temporada.TODO_EL_ANO }
    }

    override fun obtenerPorId(id: Int?): LugarTuristico? {
        if (id == null) return null
        return lugares.firstOrNull { it.id == id }
    }
}