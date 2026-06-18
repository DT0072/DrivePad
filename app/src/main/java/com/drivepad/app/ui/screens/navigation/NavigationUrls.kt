package com.drivepad.app.ui.screens.navigation

import android.net.Uri
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

internal fun buildGoogleMapsDirectionsUri(
    location: NavigationMapLocation = DefaultNavigationMapLocation,
): String {
    val destination = String.format(
        Locale.US,
        "%.6f,%.6f",
        location.latitude,
        location.longitude,
    )
    return "https://www.google.com/maps/dir/?api=1&destination=$destination&travelmode=driving"
}

internal fun buildGoogleMapsWebUrl(
    location: NavigationMapLocation = DefaultNavigationMapLocation,
    originLocation: NavigationMapLocation? = null,
): String {
    return if (originLocation != null) {
        val origin = String.format(
            Locale.US,
            "%.6f,%.6f",
            originLocation.latitude,
            originLocation.longitude,
        )
        val destination = String.format(
            Locale.US,
            "%.6f,%.6f",
            location.latitude,
            location.longitude,
        )
        "https://www.google.com/maps?saddr=$origin&daddr=$destination&dirflg=d&output=embed"
    } else {
        val query = Uri.encode(String.format(Locale.US, "%.6f,%.6f", location.latitude, location.longitude))
        "https://www.google.com/maps?q=$query&z=15&output=embed"
    }
}

internal fun buildWazeUri(
    location: NavigationMapLocation = DefaultNavigationMapLocation,
): String {
    val destination = String.format(
        Locale.US,
        "%.6f,%.6f",
        location.latitude,
        location.longitude,
    )
    return "https://waze.com/ul?ll=$destination&navigate=yes"
}
