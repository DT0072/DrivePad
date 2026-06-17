package com.drivepad.app.ui.screens.navigation

import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUrlsTest {
    @Test
    fun emptyDestination_opensMapWithoutLeavingDrivePad() {
        val url = buildNavigationUrl()

        assertTrue(url.startsWith("https://www.openstreetmap.org/export/embed.html"))
        assertTrue(url.contains("marker=3.139000%2C101.686900"))
    }

    @Test
    fun destination_buildsEmbeddedMapAroundResolvedCoordinates() {
        val url = buildNavigationUrl(
            NavigationMapLocation(
                latitude = 3.1579,
                longitude = 101.7116,
                label = "Petronas Twin Towers",
            ),
        )

        assertTrue(url.contains("marker=3.157900%2C101.711600"))
        assertTrue(url.contains("bbox=101.651600%2C3.117900%2C101.771600%2C3.197900"))
    }
}
