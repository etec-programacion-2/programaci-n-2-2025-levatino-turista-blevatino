package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

// Implementación de repositorio que carga los datos desde 'lugares.json'.
class JsonLugarTuristicoRepository : BaseLugarTuristicoRepository() {

    // Lee y deserializa el contenido del archivo JSON.
    override fun loadDataFromJson(): List<LugarTuristico> {
        val jsonFilePath = "lugares.json"

        val fileContent = try {
            // Lee el archivo desde la carpeta 'resources'
            this::class.java.classLoader.getResource(jsonFilePath)?.readText()
                ?: throw IOException("No se pudo encontrar el archivo: $jsonFilePath")
        } catch (e: Exception) {
            System.err.println("🔴 Error al cargar el archivo JSON: ${e.message}")
            return emptyList()
        }

        return try {
            // Deserializa el JSON a una lista de objetos
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }.decodeFromString<List<LugarTuristico>>(fileContent)
        } catch (e: Exception) {
            System.err.println("🔴 Error al parsear el JSON. Detalles: ${e.message}")
            return emptyList()
        }
    }
}