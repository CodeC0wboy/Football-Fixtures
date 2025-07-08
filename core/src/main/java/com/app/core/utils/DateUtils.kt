package com.app.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTime(isoDate: String): String {
    return try {
        val parsed = Instant.parse(isoDate)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        "-"
    }
}