package org.example

// Implementaciones de prueba para el repositorio y el asistente.
// Esto nos permite probar el controlador sin depender de los archivos reales.

// Repositorio de prueba que devuelve datos estáticos.
class FakeLugarTuristicoRepository : LugarTuristicoRepository {
    override fun obtenerTodos(): List<LugarTuristico> {
        return listOf(
            // Descripciones más detalladas para la prueba
            LugarTuristico("Parque A", "Un parque agradable y sencillo con zona de picnic.", "Norte", emptyList(), Temporada.VERANO),
            LugarTuristico("Museo B", "Un museo de arte contemporáneo con exposiciones rotativas.", "Centro", emptyList(), Temporada.INVIERNO)
        )
    }

    override fun buscarPorTemporada(temporada: Temporada): List<LugarTuristico> {
        return obtenerTodos().filter { it.temporadaRecomendada == temporada }
    }
}

// Asistente de prueba que ahora implementa el nuevo método de enriquecimiento.
class FakeAsistenteIA : AsistenteIA {
    override fun obtenerRespuesta(pregunta: String): String {
        return "Respuesta simulada de la IA para la pregunta: '$pregunta'."
    }

    override fun enriquecerLugarTuristico(nombre: String, descripcion: String): String {
        // Simula la respuesta enriquecida de la IA
        return "DESCRIPCIÓN POTENCIADA: ¡Experimente la majestuosidad de $nombre! Anteriormente, solo era: '$descripcion'. Ahora está listo para deslumbrar a los turistas."
    }
}

// Clase principal para la ejecución de la prueba.
fun main() {
    // 1. Instanciar las dependencias "falsas".
    val repositorioFalso = FakeLugarTuristicoRepository()
    val asistenteFalso = FakeAsistenteIA()

    // 2. Instanciar los servicios de negocio con las dependencias falsas.
    val servicioRecomendaciones = ServicioRecomendaciones(repositorioFalso)

    // 3. Crear el controlador principal, pasándole los servicios falsos.
    val controlador = ControladorPrincipal(servicioRecomendaciones, asistenteFalso)

    // --- PRUEBA DEL CONTROLADOR ---

    // Prueba 1: Solicitar recomendaciones para "verano".
    println("--- Probando solicitud de recomendaciones ---")
    val lugaresVerano = controlador.solicitarRecomendaciones(Temporada.VERANO) // Ahora usa el Enum
    println("Lugares encontrados para VERANO: ${lugaresVerano.size}")
    lugaresVerano.forEach { lugar ->
        println(" - ${lugar.nombre} (Desc: ${lugar.descripcion.substring(0, 10)}...)")
    }
    println("----------------------------------------------")

    // Prueba 2: Preguntar al asistente de IA (función chat).
    println("\n--- Probando pregunta al asistente de IA (Chat) ---")
    val respuestaIA = controlador.obtenerRespuestaAsistente("¿Qué significa la vida?")
    println("Respuesta del asistente: $respuestaIA")
    println("----------------------------------------------")

    // Prueba 3: Enriquecer un lugar turístico (Nueva funcionalidad).
    println("\n--- Probando enriquecimiento de descripción ---")
    val lugarParaEnriquecer = lugaresVerano.first() // Tomamos el primer lugar (Parque A)
    val descripcionOriginal = lugarParaEnriquecer.descripcion
    println("Descripción antes de enriquecer: ${descripcionOriginal}")

    // Llamar a la nueva función
    controlador.enriquecerDescripcionLugar(lugarParaEnriquecer)

    println("Descripción DESPUÉS de enriquecer: ${lugarParaEnriquecer.descripcion}")
    println("----------------------------------------------")

    // Prueba 4: Solicitar una temporada no válida.
    println("\n--- Probando temporada no válida ---")
    val lugaresInvalidos = controlador.solicitarRecomendaciones(Temporada.OTONO)
    println("Lugares encontrados para 'otono': ${lugaresInvalidos.size}")
    println("----------------------------------------------")
}
