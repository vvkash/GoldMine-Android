package com.goldmine.uncc.data.model

import java.time.DayOfWeek

/**
 * Mirrors the iOS `Weekday` enum. [rawValue] is kept identical to the Swift raw value so that
 * schedules exported from either platform stay interchangeable.
 */
enum class Weekday(val rawValue: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    val shortName: String get() = rawValue.take(3)

    companion object {
        fun fromRaw(raw: String): Weekday? = entries.firstOrNull { it.rawValue == raw }

        fun from(dayOfWeek: DayOfWeek): Weekday = when (dayOfWeek) {
            DayOfWeek.MONDAY -> MONDAY
            DayOfWeek.TUESDAY -> TUESDAY
            DayOfWeek.WEDNESDAY -> WEDNESDAY
            DayOfWeek.THURSDAY -> THURSDAY
            DayOfWeek.FRIDAY -> FRIDAY
            DayOfWeek.SATURDAY -> SATURDAY
            DayOfWeek.SUNDAY -> SUNDAY
        }
    }
}
