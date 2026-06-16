package com.drivepad.app.ui.screens.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal fun buildNavigationUrl(destination: String? = null): String {
    if (destination.isNullOrBlank()) {
        return "https://www.google.com/maps?output=embed&q=Current+location"
    }

    val encodedDestination = URLEncoder.encode(
        destination,
        StandardCharsets.UTF_8.name(),
    )
    return "https://www.google.com/maps?output=embed&q=$encodedDestination"
}
