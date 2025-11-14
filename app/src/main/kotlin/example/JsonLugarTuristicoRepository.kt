package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

/**
 * Implementación de LugarTuristicoRepository que carga los datos
 * desde el archivo 'lugares.json' ubicado en la carpeta de recursos.
 */
class JsonLugarTuristicoRepository : LugarTuristicoRepository {

    private val lugares: List<LugarTuristico>

    init {
        // Carga los datos la primera vez que se instancia la clase
        lugares = loadDataFromJson()
    }

    /**
     * Carga y deserializa el contenido del archivo JSON.
     * Incluye manejo de errores robusto para fallos de archivo o parsing.
     */
    private fun loadDataFromJson(): List<LugarTuristico> {
        val jsonFilePath = "lugares.json" // Archivo en la carpeta de recursos

        val fileContent = try {
            // Usa el ClassLoader para leer el archivo desde el Classpath (carpeta 'resources')
            this::class.java.classLoader.getResource(jsonFilePath)?.readText()
                ?: throw IOException("No se pudo encontrar el archivo de recursos: $jsonFilePath")
        } catch (e: Exception) {
            System.err.println("🔴 Error al cargar el archivo JSON: ${e.message}")
            return emptyList()
        }

        return try {
            // Deserializa el JSON a una lista de objetos LugarTuristico
            Json {
                ignoreUnknownKeys = true // Permite que el JSON tenga campos que no están en el data class
                isLenient = true
            }.decodeFromString<List<LugarTuristico>>(fileContent)
        } catch (e: Exception) {
            System.err.println("🔴 Error al parsear el JSON. Asegúrate de que $jsonFilePath esté bien formado. Detalles: ${e.message}")
            return emptyList()
        }
    }

    override fun obtenerTodos(): List<LugarTuristico> {
        return lugares
    }

    override fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico> {
        // Incluye lugares para la temporada seleccionada O para todo el año.
        return lugares.filter { it.temporada == temporada || it.temporada == Temporada.TODO_EL_ANO }
    }

    /**
     * [Implementación de la mejora]
     * Busca un lugar específico por su ID.
     */
    override fun obtenerPorId(id: Int?): LugarTuristico? {
        if (id == null) return null
        // Usa firstOrNull para devolver el primer lugar que coincida con el ID, o null si no se encuentra.
        return lugares.firstOrNull { it.id == id }
    }
}