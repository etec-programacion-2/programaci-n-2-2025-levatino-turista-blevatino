package org.example

// IMPORTACIONES NECESARIAS PARA CORRUTINAS Y SALIR DEL PROCESO
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess
// Nota: Las clases como JsonLugarTuristicoRepository y GeminiPythonAsistente
// se asumen en el mismo paquete org.example, por lo que no necesitan importación explícita.

/**
 * Punto de entrada principal de la aplicación.
 *
 * Utilizamos runBlocking para que el hilo principal (main) pueda llamar a funciones suspendidas
 * (como las llamadas HTTP del asistente).
 */
fun main() { // CAMBIO CLAVE: Usamos cuerpo de función explícito
    runBlocking { // Y envolvemos la lógica dentro del bloque runBlocking

        // --- 1. Inicialización de Capas de Datos y Servicios ---

        // Repositorio: Fuente de datos de los lugares turísticos (Lectura de JSON).
        val repository: LugarTuristicoRepository = JsonLugarTuristicoRepository()

        // Asistente IA: Implementación real (Ktor -> Python/OpenRouter).
        // Esta clase contiene las funciones suspendidas que requieren runBlocking.
        val asistente: AsistenteIA = GeminiPythonAsistente()

        // --- 2. Inicio de la Vista de Consola con inyección directa de dependencias ---
        val vista = VistaConsola(
            repositorio = repository,
            asistente = asistente
        )

        // Llamamos a la función suspendida iniciar()
        vista.iniciar()

        // Si el bucle de la vista termina de forma natural, la aplicación sale.
        exitProcess(0)
    }
}


