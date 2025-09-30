package org.example

import java.io.File
import java.lang.IllegalArgumentException

fun main() {
    // 1. Definimos las configuraciones de la aplicación.
    val pythonServiceUrl = "http://127.0.0.1:5000/ask"

    // 2. Cargamos la ruta del archivo JSON de forma segura.
    // Esto usa el ClassLoader para encontrar 'lugares.json' sin depender de la configuración del IDE.
    val resource = object {}.javaClass.classLoader.getResource("lugares.json")
        ?: throw IllegalArgumentException("Error fatal: Archivo 'lugares.json' no encontrado en el classpath. Revise que esté en 'src/main/resources'.")

    val jsonFilePath = resource.toURI().path

    // --- Inyección de Dependencias ---

    // Paso 3: Inicializamos las dependencias de la capa más baja.
    val asistenteIA = GeminiPythonAsistente(pythonServiceUrl)
    val repositorio = JsonLugarTuristicoRepository(jsonFilePath)

    // Paso 4: Inicializamos el servicio de negocio.
    val servicioRecomendaciones = ServicioRecomendaciones(repositorio)

    // Paso 5: Inicializamos el controlador (el intermediario).
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistenteIA)

    // Paso 6: Inicializamos la vista (la interfaz de usuario).
    val vista = VistaConsola(controlador)

    // Paso 7: Iniciamos la aplicación.
    vista.iniciar()
}