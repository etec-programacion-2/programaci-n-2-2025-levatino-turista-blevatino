package org.example

/**
 * Punto de entrada principal de la aplicación.
 * Se encarga de la inyección de dependencias y de iniciar la Vista de Consola.
 * * NOTA: Esta versión usa el JsonLugarTuristicoRepository y el GeminiPythonAsistente
 * (la implementación de red real).
 */
fun main() {

    // --- 1. Inicialización de Capas de Datos y Servicios ---

    // Repositorio: Fuente de datos de los lugares turísticos (Lee del archivo lugares.json).
    val repository: LugarTuristicoRepository = JsonLugarTuristicoRepository()

    val servicioRecomendaciones = ServicioRecomendaciones(repository)

    // Servicio de Asistente IA: Implementación real (Ktor -> Python).
    val asistente: AsistenteIA = GeminiPythonAsistente()

    // --- 2. Inicialización del Controlador Principal ---
    // El controlador une la lógica de negocio y los servicios externos.
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistente)

    // --- 3. Inicio de la Vista de Consola ---
    val vista = VistaConsola(controlador)
    vista.iniciar()
}
