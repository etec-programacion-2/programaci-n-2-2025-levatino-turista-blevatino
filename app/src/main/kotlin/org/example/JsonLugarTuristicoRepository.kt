package org.example

import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.File


class JsonLugarTuristicoRepository(private val filePath: String) : LugarTuristicoRepository {

    private val lugares: List<LugarTuristico> by lazy {
        try {
            // Usamos File para leer el archivo de la forma más robusta.
            val jsonString = File(filePath).readText()
            Json.decodeFromString<List<LugarTuristico>>(jsonString)
        } catch (e: FileNotFoundException) {
            System.err.println("Error: El archivo de datos no fue encontrado en: $filePath")
            emptyList()
        } catch (e: Exception) {
            System.err.println("Error al procesar el archivo JSON: ${e.message}")
            emptyList()
        }
    }

    override fun obtenerTodos(): List<LugarTuristico> {
        return lugares
    }

    override fun buscarPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return lugares.filter { it.temporadaRecomendada == temporada }
    }
}