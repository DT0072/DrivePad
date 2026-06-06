package com.drivepad.app.ui.screens.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUrlsTest {
    @Test
    fun emptyDestination_opensMapWithoutLeavingDrivePad() {
        assertEquals("https://www.google.com/maps", buildNavigationUrl())
    }

    @Test
    fun destination_buildsDrivingDirectionsUrl() {
        val url = buildNavigationUrl("Gas station near me")

        assertTrue(url.contains("destination=Gas+station+near+me"))
        assertTrue(url.contains("travelmode=driving"))
    }
}
