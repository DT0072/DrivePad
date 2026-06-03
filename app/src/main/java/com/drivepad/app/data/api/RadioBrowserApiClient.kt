package com.drivepad.app.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Client for the Radio Browser API (radio-browser.info).
 * Free, open-source, no API key required.
 * Provides 50,000+ radio stations worldwide.
 */
class RadioBrowserApiClient(private val httpClient: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://de1.api.radio-browser.info/json"

    suspend fun searchStations(
        query: String,
        limit: Int = 50
    ): List<RadioStation> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/stations/search") {
                parameter("name", query)
                parameter("limit", limit)
                parameter("order", "clickcount")
                parameter("reverse", "true")
                parameter("hidebroken", "true")
            }
            json.decodeFromString<List<RadioStation>>(response.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStationsByCountry(
        countryCode: String,
        limit: Int = 100
    ): List<RadioStation> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/stations/bycountrycodeexact/$countryCode") {
                parameter("limit", limit)
                parameter("order", "clickcount")
                parameter("reverse", "true")
                parameter("hidebroken", "true")
            }
            json.decodeFromString<List<RadioStation>>(response.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTopStations(limit: Int = 50): List<RadioStation> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/stations/topclick/$limit") {
                parameter("hidebroken", "true")
            }
            json.decodeFromString<List<RadioStation>>(response.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStationsByTag(
        tag: String,
        limit: Int = 50
    ): List<RadioStation> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/stations/bytag/$tag") {
                parameter("limit", limit)
                parameter("order", "clickcount")
                parameter("reverse", "true")
                parameter("hidebroken", "true")
            }
            json.decodeFromString<List<RadioStation>>(response.body())
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Serializable
data class RadioStation(
    @SerialName("stationuuid") val stationUuid: String = "",
    val name: String = "",
    @SerialName("url_resolved") val urlResolved: String = "",
    val url: String = "",
    val favicon: String = "",
    val country: String = "",
    @SerialName("countrycode") val countryCode: String = "",
    val state: String = "",
    val language: String = "",
    val tags: String = "",
    val codec: String = "",
    val bitrate: Int = 0,
    @SerialName("clickcount") val clickCount: Int = 0,
    @SerialName("votes") val votes: Int = 0,
) {
    /** Get the best available stream URL */
    val streamUrl: String get() = urlResolved.ifBlank { url }

    /** Extract a simulated FM frequency from the station name, or generate one */
    fun getDisplayFrequency(): String {
        // Try to extract a real frequency from the name (e.g., "98.8 FM", "Hitz FM 98.8")
        val freqRegex = Regex("""(\d{2,3}\.\d{1,2})""")
        val match = freqRegex.find(name)
        return match?.groupValues?.get(1) ?: ""
    }
}
