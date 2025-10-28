package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException

/**
 * Implementación de LugarTuristicoRepository que lee los datos desde
 * un archivo JSON de recursos ('lugares.json').
 */
class JsonLugarTuristicoRepository : LugarTuristicoRepository {

    private val lugares: List<LugarTuristico>

    init {
        // Bloque de inicialización: Lee y parsea el JSON al crear la instancia
        lugares = loadDataFromJson()
    }

    private fun loadDataFromJson(): List<LugarTuristico> {
        val jsonFilePath = "lugares.json" // Nombre del archivo en la carpeta de recursos

        // Intenta obtener el contenido del archivo desde los recursos
        val fileContent = try {
            this::class.java.classLoader.getResource(jsonFilePath)?.readText()
                ?: throw IOException("No se pudo encontrar el archivo de recursos: $jsonFilePath")
        } catch (e: Exception) {
            System.err.println("Error al cargar el archivo JSON: ${e.message}")
            return emptyList()
        }

        // Intenta parsear el JSON
        return try {
            // Se usa el parser de Kotlinx Serialization para convertir el JSON en List<LugarTuristico>
            Json { ignoreUnknownKeys = true }.decodeFromString<List<LugarTuristico>>(fileContent)
        } catch (e: Exception) {
            System.err.println("Error al parsear el JSON: ${e.message}")
            return emptyList()
        }
    }

    override fun obtenerTodos(): List<LugarTuristico> {
        return lugares
    }

    override fun obtenerPorTemporada(temporada: Temporada): List<LugarTuristico> {
        // Incluye lugares que son para todo el año
        return lugares.filter { it.temporada == temporada || it.temporada == Temporada.TODO_EL_ANO }
    }
}