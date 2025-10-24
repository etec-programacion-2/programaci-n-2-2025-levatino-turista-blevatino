package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDate

// DTO para parsear la respuesta de la API de WorldTimeAPI
@Serializable
private data class WorldTimeResponse(
    val datetime: String, // La hora y fecha en formato ISO 8601
    val timezone: String
)

/**
 * Implementación REAL de ServicioMeteorologico.
 * - Utiliza Ktor para consultar la hora real de Mendoza de un servicio público (WorldTimeAPI).
 * - Mantiene el pronóstico del clima estático, pero sin latencia artificial.
 */
class ServicioMeteorologicoReal : ServicioMeteorologico {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignora campos que no necesitamos
                isLenient = true
            })
        }
    }

    // API de tiempo real para la zona horaria de Mendoza.
    private val TIME_API_URL = "http://worldtimeapi.org/api/timezone/America/Argentina/Mendoza"
    private val mendozaZoneId = "America/Argentina/Buenos_Aires" // Zona horaria usada para formateo local.

    /**
     * Obtiene la hora actual REAL para Mendoza, Argentina, usando una API pública.
     */
    override suspend fun obtenerHoraActual(): String {
        return try {
            val response = client.get(TIME_API_URL)

            if (response.status.isSuccess()) {
                val timeData = response.body<WorldTimeResponse>()

                // Parsea la cadena ISO y la reformatea a la zona de Mendoza.
                val zdt = ZonedDateTime.parse(timeData.datetime)
                    .withZoneSameInstant(java.time.ZoneId.of(mendozaZoneId))

                // Formato: 24/10/2025 08:10:00 AM ART
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a z")
                zdt.format(formatter)
            } else {
                "Error al consultar la hora real (HTTP ${response.status.value})"
            }
        } catch (e: Exception) {
            "Error de red al consultar la hora: ${e.message}"
        }
    }

    /**
     * Genera un pronóstico simulado rápido (sin latencia artificial) para 5 días.
     * Mantenemos esto estático por simplicidad y falta de una clave de API pública.
     */
    override suspend fun obtenerPronosticoClima(): List<PronosticoDia> {
        val today = LocalDate.now(java.time.ZoneId.of(mendozaZoneId))
        val formatter = DateTimeFormatter.ofPattern("dd/MM")

        // Datos estáticos (pero rápidos)
        val condiciones = listOf(
            "Soleado y cálido",
            "Parcialmente nublado",
            "Tormentas aisladas",
            "Frío y ventoso",
            "Despejado, sube temperatura"
        )
        val temperaturasMax = listOf("32°C", "28°C", "24°C", "24°C", "25°C")
        val temperaturasMin = listOf("18°C", "16°C", "14°C", "10°C", "11°C")

        val pronostico = mutableListOf<PronosticoDia>()

        for (i in 0..4) {
            val date = today.plus(i.toLong(), ChronoUnit.DAYS)
            pronostico.add(
                PronosticoDia(
                    dia = date.format(formatter),
                    condicion = condiciones[i],
                    tempMax = temperaturasMax[i],
                    tempMin = temperaturasMin[i],
                    esActual = (i == 0)
                )
            )
        }

        return pronostico
    }
}
