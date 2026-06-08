package com.drivepad.app.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Client for the Open-Meteo free weather API.
 * No API key required.
 */
class WeatherApiClient(private val httpClient: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherData? {
        return try {
            val response: HttpResponse = httpClient.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("current", "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,is_day")
                parameter("timezone", "auto")
            }
            val body = response.body<String>()
            val result = json.decodeFromString<OpenMeteoResponse>(body)
            result.current?.let { current ->
                WeatherData(
                    temperature = current.temperature,
                    humidity = current.humidity,
                    weatherCode = current.weatherCode,
                    windSpeed = current.windSpeed,
                    isDay = current.isDay == 1,
                    description = weatherCodeToDescription(current.weatherCode),
                    icon = weatherCodeToIcon(current.weatherCode, current.isDay == 1),
                    observedAt = current.time,
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun weatherCodeToDescription(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing Rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow Grains"
        80, 81, 82 -> "Rain Showers"
        85, 86 -> "Snow Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm + Hail"
        else -> "Unknown"
    }

    private fun weatherCodeToIcon(code: Int, isDay: Boolean): String = when (code) {
        0 -> if (isDay) "☀️" else "🌙"
        1, 2 -> if (isDay) "⛅" else "☁️"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "🌧️"
        66, 67 -> "🌨️"
        71, 73, 75, 77, 85, 86 -> "❄️"
        95, 96, 99 -> "⛈️"
        else -> "🌤️"
    }
}

@Serializable
data class OpenMeteoResponse(
    val current: CurrentWeather? = null
)

@Serializable
data class CurrentWeather(
    val time: String = "",
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("relative_humidity_2m") val humidity: Int,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("is_day") val isDay: Int
)

data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double,
    val isDay: Boolean,
    val description: String,
    val icon: String,
    val observedAt: String,
)
