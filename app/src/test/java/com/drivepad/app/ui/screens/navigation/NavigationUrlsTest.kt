package com.drivepad.app.ui.screens.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUrlsTest {
    @Test
    fun emptyDestination_opensMapWithoutLeavingDrivePad() {
        assertEquals(
            "https://www.openstreetmap.org/#map=13/3.1390/101.6869",
            buildNavigationUrl(),
        )
    }

    @Test
    fun destination_buildsEmbeddedSearchUrl() {
        val url = buildNavigationUrl("Gas station near me")

        assertTrue(url.startsWith("https://www.openstreetmap.org/search"))
        assertTrue(url.contains("query=Gas+station+near+me"))
    }
}
