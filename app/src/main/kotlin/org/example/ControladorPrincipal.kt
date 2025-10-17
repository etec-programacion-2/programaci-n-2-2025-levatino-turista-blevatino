package org.example

import java.io.IOException
import java.lang.Exception

class ControladorPrincipal(
    private val servicioRecomendaciones: ServicioRecomendaciones,
    private val asistenteIA: AsistenteIA
) {

    fun solicitarRecomendaciones(temporada: Temporada): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerRecomendacionesPorTemporada(temporada)
    }

    fun solicitarTodosLosLugares(): List<LugarTuristico> {
        return servicioRecomendaciones.obtenerTodos()
    }

    // MARCADA COMO SUSPEND para poder llamar al asistenteIA.obtenerRespuesta
    suspend fun obtenerRespuestaAsistente(pregunta: String): String {
        return try {
            asistenteIA.obtenerRespuesta(pregunta)
        } catch (e: IOException) {
            "Error de red: No se pudo contactar al asistente. Mensaje: ${e.message}"
        } catch (e: Exception) {
            "Error interno del asistente: ${e.message}"
        }
    }

    // Función para enriquecer el lugar (llamada al servicio de red)
    suspend fun enriquecerDescripcionLugar(indiceSeleccionado: Int): String {
        val lugar = servicioRecomendaciones.obtenerLugarPorIndice(indiceSeleccionado)

        if (lugar == null) {
            return "Error: Índice de lugar no válido."
        }

        try {
            val nuevaDescripcion = asistenteIA.enriquecerLugarTuristico(
                nombre = lugar.nombre,
                descripcion = lugar.descripcion
            )

            // Actualiza el objeto original en memoria
            lugar.descripcion = nuevaDescripcion

            return "Descripción de '${lugar.nombre}' enriquecida exitosamente."

        } catch (e: Exception) {
            return "Error al enriquecer la descripción de '${lugar.nombre}': ${e.message}"
        }
    }
}

