package com.drivepad.app.data.api

import org.junit.Assert.assertEquals
import org.junit.Test

class RadioStationTest {
    @Test
    fun streamUrl_prefersResolvedUrl() {
        val station = RadioStation(
            urlResolved = "https://stream.example/resolved",
            url = "https://stream.example/original",
        )

        assertEquals("https://stream.example/resolved", station.streamUrl)
    }

    @Test
    fun displayFrequency_extractsDecimalFrequencyFromName() {
        val station = RadioStation(name = "Drive FM 98.8")

        assertEquals("98.8", station.getDisplayFrequency())
    }
}
