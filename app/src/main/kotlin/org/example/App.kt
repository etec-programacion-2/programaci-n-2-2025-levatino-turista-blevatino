package org.example

// --- Importaciones de DTOs ahora separados ---
import org.example.Mensaje
import org.example.RespuestaIA
import org.example.PeticionEnriquecimiento
import org.example.Temporada
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess
import kotlinx.serialization.Serializable

/**
 * Punto de entrada principal de la aplicación.
 * Se encarga de la inyección de dependencias y de iniciar la Vista de Consola.
 */
fun main() = runBlocking { // Usamos runBlocking para envolver la aplicación de consola.

    // --- 1. Inicialización de Capas de Datos y Servicios ---

    // Repositorio: Fuente de datos de los lugares turísticos (Lee del archivo lugares.json).
    val repository: LugarTuristicoRepository = JsonLugarTuristicoRepository()

    // Servicio de Negocio de Recomendaciones.
    val servicioRecomendaciones = ServicioRecomendaciones(repository)

    // Servicio de Asistente IA: Implementación real (Ktor -> Python).
    val asistente: AsistenteIA = GeminiPythonAsistente()

    // Servicio Meteorológico (Implementación Real que usa Ktor).
    val servicioMeteorologico: ServicioMeteorologico = ServicioMeteorologicoReal()

    // --- 2. Inicialización del Controlador Principal ---
    // El controlador une la lógica de negocio y los servicios externos (DIP).
    val controlador = ControladorPrincipal(
        servicioRecomendaciones,
        asistente,
        servicioMeteorologico
    )

    // --- 3. Inicio de la Vista de Consola ---
    val vista = VistaConsola(controlador)
    vista.iniciar()
}



