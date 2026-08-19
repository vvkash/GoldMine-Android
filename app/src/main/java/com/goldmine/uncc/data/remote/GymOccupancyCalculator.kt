package com.goldmine.uncc.data.remote

import com.goldmine.uncc.data.model.GymOccupancy
import java.time.DayOfWeek
import java.time.LocalDateTime

/**
 * Predicted UREC busyness.
 *
 * Ported verbatim from the iOS `GymOccupancyService`: hour-by-hour occupancy curves for
 * weekdays, Saturday and Sunday, plus the same operating-hours rules.
 */
object GymOccupancyCalculator {

    private const val MAX_CAPACITY = 100
    private const val DEFAULT_OCCUPANCY = 15

    /** Sunday = 0, Monday = 1 … Saturday = 6, matching the Swift indices. */
    private val weekdayPatterns: Map<Int, Map<Int, Int>> = mapOf(
        1 to mapOf(
            6 to 25, 7 to 45, 8 to 35, 9 to 20, 10 to 15, 11 to 25,
            12 to 40, 13 to 35, 14 to 25, 15 to 30, 16 to 55, 17 to 85,
            18 to 95, 19 to 75, 20 to 45, 21 to 25, 22 to 15,
        ),
        6 to mapOf(
            9 to 15, 10 to 25, 11 to 35, 12 to 45, 13 to 50, 14 to 45,
            15 to 40, 16 to 35, 17 to 30, 18 to 25, 19 to 20, 20 to 15,
        ),
        0 to mapOf(
            9 to 10, 10 to 20, 11 to 30, 12 to 40, 13 to 45, 14 to 40,
            15 to 35, 16 to 30, 17 to 25, 18 to 20, 19 to 15, 20 to 10,
        ),
    )

    fun occupancyAt(now: LocalDateTime = LocalDateTime.now()): GymOccupancy {
        val rawWeekday = when (now.dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.SATURDAY -> 6
            else -> now.dayOfWeek.value // Monday = 1 … Friday = 5
        }
        // Weekdays all share Monday's curve.
        val patternKey = if (rawWeekday in 1..5) 1 else rawWeekday
        val hour = now.hour

        val isWeekend = rawWeekday == 0 || rawWeekday == 6
        val openingHour = if (isWeekend) 9 else 6
        val closingHour = if (isWeekend) 20 else 22

        val isOpen = hour in openingHour until closingHour
        val occupancy = if (isOpen) {
            weekdayPatterns[patternKey]?.get(hour) ?: DEFAULT_OCCUPANCY
        } else {
            0
        }

        return GymOccupancy(
            currentOccupancy = occupancy,
            maxCapacity = MAX_CAPACITY,
            dayOfWeek = patternKey,
            hourOfDay = hour,
            isClosed = !isOpen,
        )
    }

    /** The full curve for the current day type, used by the detail screen's chart. */
    fun todaysCurve(now: LocalDateTime = LocalDateTime.now()): List<Pair<Int, Int>> {
        val rawWeekday = when (now.dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.SATURDAY -> 6
            else -> 1
        }
        return weekdayPatterns[rawWeekday].orEmpty().toList().sortedBy { it.first }
    }
}
