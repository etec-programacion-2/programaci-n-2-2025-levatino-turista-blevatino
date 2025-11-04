package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

class JsonLugarTuristicoRepository : LugarTuristicoRepository {

    private val lugares: List<LugarTuristico>

    init {
        lugares = loadDataFromJson()
    }

    private fun loadDataFromJson(): List<LugarTuristico> {
        val jsonFilePath = "lugares.json" // Archivo en la carpeta de recursos

        val fileContent = try {
            this::class.java.classLoader.getResource(jsonFilePath)?.readText()
                ?: throw IOException("No se pudo encontrar el archivo de recursos: $jsonFilePath")
        } catch (e: Exception) {
            System.err.println("Error al cargar el archivo JSON: ${e.message}")
            return emptyList()
        }

        return try {
            Json { ignoreUnknownKeys = true }.decodeFromString<List<LugarTuristico>>(fileContent)
        } catch (e: Exception) {
            System.err.println("Error al parsear el JSON. Asegúrate de que $jsonFilePath esté bien formado. ${e.message}")
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
}