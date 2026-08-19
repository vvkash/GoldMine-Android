package com.goldmine.uncc.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

/**
 * A single scheduled class.
 *
 * Times are persisted as minutes-since-midnight instead of the absolute `Date` values the iOS
 * app stores. The iOS code only ever reads the hour/minute components, so this removes the
 * timezone drift that the original model is subject to while keeping identical behaviour.
 */
@Serializable
data class ClassItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val buildingName: String,
    val roomNumber: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val days: Set<Weekday>,
    val color: String,
)

/** Campus building coordinates, ported from the iOS `CampusBuildings` table. */
object CampusBuildings {
    val buildings: Map<String, LatLng> = linkedMapOf(
        "Atkins Library" to LatLng(35.3055, -80.7322),
        "Student Union" to LatLng(35.3088, -80.7346),
        "Fretwell" to LatLng(35.3071, -80.7311),
        "Colvard" to LatLng(35.3059, -80.7325),
        "Woodward" to LatLng(35.3075, -80.7351),
        "Burson" to LatLng(35.3065, -80.7302),
        "Kennedy" to LatLng(35.3061, -80.7285),
        "Cone University Center" to LatLng(35.3059, -80.7334),
        "Robinson Hall" to LatLng(35.3078, -80.7295),
        "Storrs" to LatLng(35.3081, -80.7296),
        "Cameron Hall" to LatLng(35.3067, -80.7335),
        "College of Health & Human Services" to LatLng(35.3066, -80.7353),
        "Barnard" to LatLng(35.3054, -80.7304),
        "Bioinformatics" to LatLng(35.3126, -80.7409),
        "EPIC" to LatLng(35.3111, -80.7418),
        "McEniry" to LatLng(35.3066, -80.7318),
        "Grigg Hall" to LatLng(35.3115, -80.7400),
        "Duke Centennial Hall" to LatLng(35.3120, -80.7410),
        "Smith" to LatLng(35.3079, -80.7284),
        "Rowe" to LatLng(35.3048, -80.7342),
        "Cato College of Education" to LatLng(35.3057, -80.7373),
        "Denny" to LatLng(35.3043, -80.7364),
        "Friday" to LatLng(35.3052, -80.7343),
        "Belk Gym" to LatLng(35.3035, -80.7322),
        "UREC" to LatLng(35.3073, -80.7388),
    )

    val names: List<String> = buildings.keys.sorted()

    fun coordinateFor(buildingName: String): LatLng? = buildings[buildingName]
}

/** Returns the classes scheduled on [date], mirroring `ClassManager.getClassesFor(date:)`. */
fun List<ClassItem>.classesOn(date: LocalDate): List<ClassItem> {
    val weekday = Weekday.from(date.dayOfWeek)
    return filter { weekday in it.days }
}
