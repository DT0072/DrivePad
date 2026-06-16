package com.drivepad.app.ui.screens.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUrlsTest {
    @Test
    fun emptyDestination_opensMapWithoutLeavingDrivePad() {
        assertEquals(
            "https://www.google.com/maps?output=embed&q=Current+location",
            buildNavigationUrl(),
        )
    }

    @Test
    fun destination_buildsEmbeddedSearchUrl() {
        val url = buildNavigationUrl("Gas station near me")

        assertTrue(url.contains("output=embed"))
        assertTrue(url.contains("q=Gas+station+near+me"))
    }
}
