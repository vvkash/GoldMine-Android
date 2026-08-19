package com.goldmine.uncc.data.model

import kotlinx.serialization.Serializable

/**
 * A configurable shortcut on the home grid. Ids and default ordering are identical to the iOS
 * `HomeButton.allButtons` list so a user's layout feels the same on both platforms.
 */
@Serializable
data class HomeButton(
    val id: String,
    val name: String,
    val isVisible: Boolean = true,
    val order: Int,
) {
    companion object {
        const val GYM = "gym"
        const val STUDY_ROOM = "studyroom"
        const val EVENTS = "events"
        const val PARKING = "parking"
        const val BUS = "bus"
        const val DISCOUNTS = "discounts"
        const val CLASSES = "classes"
        const val CAMPUS_MAP = "campusmap"
        const val EATS = "eats"
        const val SPORTS = "sports"

        val defaults: List<HomeButton> = listOf(
            HomeButton(GYM, "UREC Status", order = 0),
            HomeButton(STUDY_ROOM, "Study Room", order = 1),
            HomeButton(EVENTS, "Events", order = 2),
            HomeButton(PARKING, "Parking", order = 3),
            HomeButton(BUS, "Bus", order = 4),
            HomeButton(DISCOUNTS, "Discounts", order = 5),
            HomeButton(CLASSES, "Classes", order = 6),
            HomeButton(CAMPUS_MAP, "Campus Map", order = 7),
            HomeButton(EATS, "Dining", order = 8),
            HomeButton(SPORTS, "Sports", order = 9),
        )
    }
}
