package org.example

import java.io.File

fun main() {
    // Definimos las URL y la ruta del archivo, que son las configuraciones de la aplicación.
    val pythonServiceUrl = "http://127.0.0.1:5000/ask"
    val jsonFilePath = "src/main/resources/lugares.json"

    // Paso 1: Inyección de Dependencias
    // Se crean las instancias de las dependencias de la capa más baja.
    val asistenteIA = GeminiPythonAsistente(pythonServiceUrl)
    val repositorio = JsonLugarTuristicoRepository(jsonFilePath)

    // Paso 2: Creamos los objetos de la lógica de negocio, inyectando sus dependencias.
    // El ServicioRecomendaciones necesita el Repositorio para funcionar.
    val servicioRecomendaciones = ServicioRecomendaciones(repositorio)

    // Paso 3: Creamos el controlador, inyectando sus servicios de negocio.
    // El ControladorPrincipal necesita el ServicioRecomendaciones y el AsistenteIA.
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistenteIA)

    // Paso 4: Creamos la vista, inyectando el controlador.
    // La VistaConsola necesita el ControladorPrincipal para delegar las acciones del usuario.
    val vista = VistaConsola(controlador)

    // Paso 5: Iniciamos la aplicación llamando al método principal de la vista.
    vista.iniciar()
}
