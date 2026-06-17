package com.drivepad.app.ui.screens.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal fun buildNavigationUrl(destination: String? = null): String {
    if (destination.isNullOrBlank()) {
        return "https://www.openstreetmap.org/#map=13/3.1390/101.6869"
    }

    val encodedDestination = URLEncoder.encode(
        destination,
        StandardCharsets.UTF_8.name(),
    )
    return "https://www.openstreetmap.org/search?query=$encodedDestination"
}
