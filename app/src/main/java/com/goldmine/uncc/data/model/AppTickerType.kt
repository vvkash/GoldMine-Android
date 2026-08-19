package com.goldmine.uncc.data.model

/** Which widget the home screen ticker shows by default (iOS `AppTickerType`). */
enum class AppTickerType(val rawValue: String) {
    UREC_STATUS("Gym Status"),
    TODAYS_CLASSES("Today's Classes");

    companion object {
        fun fromRaw(raw: String?): AppTickerType =
            entries.firstOrNull { it.rawValue == raw } ?: UREC_STATUS
    }
}
