package org.example
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.IOException

// Simulación de tu modelo de datos del lugar turístico
data class LugarTuristico(val nombre: String, var descripcion: String)

fun main() {
    // 1. Inicializa el servicio
    val servicioIA = AsistenteQwenService()

    // 2. Simula un lugar de tu base de datos
    val lugarDeBD = LugarTuristico("Puente del Inca", "Es un puente natural en la ruta a Chile.")

    println("Descripción original de ${lugarDeBD.nombre}:\n-> ${lugarDeBD.descripcion}")

    // La llamada debe hacerse dentro de un contexto de Coroutine (como un ViewModelScope.launch en Android)
    runBlocking(Dispatchers.IO) {
        println("\nIniciando petición de enriquecimiento a la IA...")

        try {
            // 3. Llama a la función de enriquecimiento
            val nuevaDescripcion = servicioIA.enriquecerDescripcion(
                nombre = lugarDeBD.nombre,
                descripcion = lugarDeBD.descripcion
            )

            // 4. Actualiza la descripción en el modelo
            lugarDeBD.descripcion = nuevaDescripcion

            println("\n--- DESCRIPCIÓN ENRIQUECIDA EXITOSAMENTE ---")
            println("Nueva descripción de ${lugarDeBD.nombre}:")
            println("-> $nuevaDescripcion")

        } catch (e: Exception) {
            println("\n!!! ERROR AL ENRIQUECER !!!")
            println("Detalle del error: ${e.message}")
        }
    }
}