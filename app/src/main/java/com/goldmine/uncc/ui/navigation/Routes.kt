package com.goldmine.uncc.ui.navigation

/** Every web-backed campus resource the app links to (ported from the iOS WebView screens). */
enum class WebDestination(
    val key: String,
    val title: String,
    val url: String,
    val zoomScale: Float = 1f,
) {
    STUDY_ROOM("studyroom", "Study Room Reservations", "https://atkinsreservations.charlotte.edu/"),
    EVENTS("events", "Campus Events", "https://campusevents.charlotte.edu/"),
    PARKING("parking", "Campus Parking", "https://parkingavailability.charlotte.edu/"),
    BUS("bus", "Charlotte Bus", "https://charlotte.passiogo.com/"),
    SPORTS("sports", "Charlotte 49ers", "https://charlotte49ers.com/calendar"),
    DINING("dining", "Campus Dining", "https://dineoncampus.com/unccharlotte/whats-on-the-menu"),
    CLUBS("clubs", "Clubs & Organizations", "https://ninerengage.charlotte.edu/organizations"),
    UREC_WIDGET(
        key = "urecwidget",
        title = "UREC Live Count",
        url = "https://www.connect2mycloud.com/Widgets/Data/locationCount" +
            "?type=circle&key=1a0b4030-78cb-4f32-90e5-3a041ac6b640",
        zoomScale = 1.7f,
    );

    companion object {
        fun fromKey(key: String?): WebDestination = entries.firstOrNull { it.key == key } ?: EVENTS
    }
}

object Routes {
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    const val CAMPUS_MAP = "campusMap"
    const val CLASSES = "classes"
    const val CLASSES_MAP = "classesMap"
    const val ADD_CLASS = "addClass"
    const val UREC = "urec"
    const val DISCOUNTS = "discounts"
    const val DINING = "dining"
    const val PRIVACY = "privacy"
    const val FREEBIE_MAP = "freebieMap"
    const val ADD_FREEBIE = "addFreebie"

    private const val WEB_BASE = "web"
    const val WEB = "$WEB_BASE/{key}"
    fun web(destination: WebDestination): String = "$WEB_BASE/${destination.key}"

    private const val EDIT_CLASS_BASE = "editClass"
    const val EDIT_CLASS = "$EDIT_CLASS_BASE/{classId}"
    fun editClass(classId: String): String = "$EDIT_CLASS_BASE/$classId"
}
