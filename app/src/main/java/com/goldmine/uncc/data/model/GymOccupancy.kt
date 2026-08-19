package com.goldmine.uncc.data.model

/** Snapshot of UREC busyness, ported from the iOS `GymOccupancy` struct. */
data class GymOccupancy(
    val currentOccupancy: Int,
    val maxCapacity: Int,
    val dayOfWeek: Int,
    val hourOfDay: Int,
    val isClosed: Boolean = false,
) {
    val occupancyPercentage: Double
        get() = if (maxCapacity == 0) 0.0 else currentOccupancy.toDouble() / maxCapacity * 100

    val statusDescription: String
        get() = when {
            isClosed -> "Closed"
            occupancyPercentage < 30 -> "Not busy"
            occupancyPercentage < 60 -> "Moderately busy"
            occupancyPercentage < 80 -> "Busy"
            else -> "Very busy"
        }
}

/** Wording used by the badge/ticker views on iOS (85% cutoff rather than 80%). */
fun busynessText(percentage: Double): String = when {
    percentage < 30 -> "Not busy"
    percentage < 60 -> "Moderately busy"
    percentage < 85 -> "Busy"
    else -> "Very busy"
}
