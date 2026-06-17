package com.drivepad.app.ui.screens.navigation

import java.util.Locale

data class NavigationMapLocation(
    val latitude: Double,
    val longitude: Double,
    val label: String,
)

val DefaultNavigationMapLocation = NavigationMapLocation(
    latitude = 3.1390,
    longitude = 101.6869,
    label = "Kuala Lumpur",
)

internal fun buildNavigationUrl(
    location: NavigationMapLocation = DefaultNavigationMapLocation,
): String {
    val longitudeSpan = 0.06
    val latitudeSpan = 0.04
    val boundingBox = listOf(
        location.longitude - longitudeSpan,
        location.latitude - latitudeSpan,
        location.longitude + longitudeSpan,
        location.latitude + latitudeSpan,
    ).joinToString("%2C") { String.format(Locale.US, "%.6f", it) }
    val marker = String.format(
        Locale.US,
        "%.6f%%2C%.6f",
        location.latitude,
        location.longitude,
    )

    return "https://www.openstreetmap.org/export/embed.html" +
        "?bbox=$boundingBox&layer=mapnik&marker=$marker"
}
