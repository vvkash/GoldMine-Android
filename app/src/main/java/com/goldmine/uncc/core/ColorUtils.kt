package com.goldmine.uncc.core

import androidx.compose.ui.graphics.Color
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Parses `#RRGGBB` / `RRGGBB` strings, falling back to grey exactly like the iOS `Color(hex:)`. */
fun colorFromHex(hex: String): Color {
    val sanitized = hex.trim().removePrefix("#")
    val rgb = sanitized.toLongOrNull(16) ?: return Color.Gray
    return Color(
        red = ((rgb and 0xFF0000) shr 16) / 255f,
        green = ((rgb and 0x00FF00) shr 8) / 255f,
        blue = (rgb and 0x0000FF) / 255f,
    )
}

/** Inverse of [colorFromHex] — mirrors the iOS `Color.hex` computed property. */
fun Color.toHexString(): String = String.format(
    Locale.US,
    "#%02X%02X%02X",
    (red * 255).toInt().coerceIn(0, 255),
    (green * 255).toInt().coerceIn(0, 255),
    (blue * 255).toInt().coerceIn(0, 255),
)

private val shortTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("h:mm a", Locale.US)

/** Formats minutes-since-midnight the way `DateFormatter.timeStyle = .short` does. */
fun formatMinutesOfDay(minutesOfDay: Int): String {
    val clamped = ((minutesOfDay % (24 * 60)) + 24 * 60) % (24 * 60)
    return LocalTime.of(clamped / 60, clamped % 60).format(shortTimeFormatter)
}
