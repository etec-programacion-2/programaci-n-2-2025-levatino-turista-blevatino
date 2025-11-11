package org.example

import java.io.IOException

/**
 * Orquestador central de la lógica de la aplicación.
 * Gestiona el flujo de trabajo, decide qué servicio llamar (local o IA).
 */
class ControladorPrincipal(
    val servicioRecomendaciones: ServicioRecomendaciones,
    val asistenteIA: AsistenteIA
) {

    // --- FUNCIONES DE SERVICIO LOCAL ---

    /**
     * [CORRECCIÓN DE LA LÍNEA 17] Llama a la función que antes generaba el error.
     */
    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    /**
     * Lógica para enriquecer la descripción de un lugar usando la IA.
     */
    suspend fun enriquecerDescripcionLugar(lugar: LugarTuristico) {
        val descripcionEnriquecida = try {
            asistenteIA.enriquecerLugarTuristico(
                lugar.nombre,
                lugar.descripcion
            )
        } catch (e: IOException) {
            // Manejo de error específico de la comunicación con Python
            println("ERROR COMUNICACIÓN IA: Fallo al enriquecer. ${e.message}")
            // Devolvemos la descripción original o un mensaje de error si falla la comunicación
            return
        }

        // [ROBUSTEZ] Usa la función de limpieza para manejar prefijos de forma segura.
        lugar.descripcion = limpiarPrefijoIA(descripcionEnriquecida)
    }

    // Función auxiliar para limpieza de prefijos (maneja mayúsculas/minúsculas)
    private fun limpiarPrefijoIA(texto: String): String {
        val prefijoPotenciado = "PotenciadoIA:"
        val prefijoBase = "BaseDeDatos:"

        var resultado = texto

        // 1. Comprobar y eliminar 'PotenciadoIA:' (ignora mayúsculas/minúsculas)
        if (resultado.startsWith(prefijoPotenciado, ignoreCase = true)) {
            resultado = resultado.substringAfter(prefijoPotenciado)
        }

        // 2. Comprobar y eliminar 'BaseDeDatos:' (ignora mayúsculas/minúsculas)
        if (resultado.startsWith(prefijoBase, ignoreCase = true)) {
            resultado = resultado.substringAfter(prefijoBase)
        }

        return resultado.trim()
    }
}