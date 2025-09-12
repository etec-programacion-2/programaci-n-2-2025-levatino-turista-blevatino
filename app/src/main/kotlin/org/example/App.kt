package org.example

import java.io.File

fun main() {
    // Definimos la URL de nuestro servidor de Python
    val pythonServiceUrl = "http://127.0.0.1:5000/ask"

    // Paso 1: Inicializar las dependencias con sus implementaciones reales.
    val asistenteIA = GeminiPythonAsistente(pythonServiceUrl)

    // Cargar el archivo JSON desde el classpath de forma segura.
    val jsonFile = File(
        object {}.javaClass.classLoader.getResource("lugares.json")?.path
            ?: throw IllegalArgumentException("Archivo de datos JSON no encontrado.")
    )

    val repositorio = JsonLugarTuristicoRepository(jsonFile.path)

    // Paso 2: Crear los servicios de negocio con sus dependencias.
    val servicioRecomendaciones = ServicioRecomendaciones(repositorio)

    // Paso 3: Crear el controlador principal.
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistenteIA)

    // --- PRUEBA DEL FLUJO COMPLETO ---

    // Prueba de recomendación: busca un lugar para el verano.
    println("--- Buscando recomendaciones para VERANO ---")
    val recomendaciones = controlador.solicitarRecomendaciones("VERANO")
    if (recomendaciones.isNotEmpty()) {
        println("Recomendaciones encontradas:")
        recomendaciones.forEach { lugar ->
            println("  - ${lugar.nombre}")
        }
    } else {
        println("No se encontraron recomendaciones para la temporada. Verifique el archivo JSON.")
    }

    // Prueba de IA: hace una pregunta al asistente.
    println("\n--- Preguntando a la IA ---")
    val preguntaUsuario = "¿Cuál es el lugar turístico más popular de Mendoza?"
    val respuestaIA = controlador.preguntarAlAsistente(preguntaUsuario)
    println("Pregunta: $preguntaUsuario")
    println("Respuesta de la IA: $respuestaIA")

    // --- FIN DE LA PRUEBA ---
}
