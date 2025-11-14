package org.example

import java.io.IOException

// Controlador de la aplicación.
// Conecta la vista/API con los servicios de negocio y la IA.
class ControladorPrincipal(
    val servicioRecomendaciones: ServicioRecomendaciones,
    val asistenteIA: AsistenteIA
) {

    // --- Funciones de Acceso a Servicios ---

    // Obtiene recomendaciones filtradas por temporada.
    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    // Obtiene una respuesta del chat IA.
    suspend fun solicitarRespuestaChat(historialMensajes: List<Mensaje>): String {
        return asistenteIA.obtenerRespuesta(historialMensajes)
    }

    // Envía un lugar a la IA para enriquecer su descripción y devuelve el objeto modificado.
    suspend fun enriquecerDescripcionLugar(lugar: LugarTuristico): LugarTuristico {
        val descripcionEnriquecida = try {
            asistenteIA.enriquecerLugarTuristico(
                lugar.nombre,
                lugar.descripcion
            )
        } catch (e: IOException) {
            println("ERROR COMUNICACIÓN IA: Fallo al enriquecer. ${e.message}")
            throw e
        }

        // Limpia cualquier prefijo que la IA haya incluido
        val nuevaDescripcion = limpiarPrefijoIA(descripcionEnriquecida)

        // Actualiza y retorna el objeto
        lugar.descripcion = nuevaDescripcion
        return lugar
    }

    // Función auxiliar para limpiar prefijos como "PotenciadoIA:"
    private fun limpiarPrefijoIA(texto: String): String {
        val prefijoPotenciado = "PotenciadoIA:"
        val prefijoBase = "BaseDeDatos:"

        var resultado = texto.trim()

        // Elimina prefijos sin distinción de mayúsculas
        if (resultado.startsWith(prefijoPotenciado, ignoreCase = true)) {
            resultado = resultado.substringAfter(prefijoPotenciado)
        }
        if (resultado.startsWith(prefijoBase, ignoreCase = true)) {
            resultado = resultado.substringAfter(prefijoBase)
        }

        return resultado.trim()
    }
}