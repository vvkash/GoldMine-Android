package com.goldmine.uncc.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * Building pins shown on the campus map, ported from the iOS `CampusMapBuildings` table.
 * (Kept separate from [CampusBuildings], which the class scheduler uses — the two lists differ
 * in the original app.)
 */
object CampusMapBuildings {
    val all: Map<String, LatLng> = linkedMapOf(
        "Student Union" to LatLng(35.3085, -80.7339),
        "Atkins Library" to LatLng(35.3059, -80.7322),
        "Kennedy" to LatLng(35.3050, -80.7318),
        "Cato College of Education" to LatLng(35.3066, -80.7349),
        "Burson" to LatLng(35.3073, -80.7313),
        "Fretwell" to LatLng(35.3072, -80.7300),
        "College of Health and Human Services" to LatLng(35.3066, -80.7288),
        "Barnard" to LatLng(35.3042, -80.7296),
        "Woodward Hall" to LatLng(35.3077, -80.7349),
        "Storrs" to LatLng(35.3094, -80.7338),
        "McEniry" to LatLng(35.3068, -80.7303),
        "Denny" to LatLng(35.3041, -80.7313),
        "Reese" to LatLng(35.3033, -80.7309),
        "College of Computing" to LatLng(35.3086, -80.7357),
        "Cameron Hall" to LatLng(35.3051, -80.7330),
        "Colvard" to LatLng(35.3055, -80.7338),
        "Robinson Hall" to LatLng(35.3049, -80.7342),
        "Rowe" to LatLng(35.3037, -80.7326),
        "Epic" to LatLng(35.3109, -80.7414),
        "Portal" to LatLng(35.3117, -80.7422),
        "UREC" to LatLng(35.3081, -80.7368),
        "Student Activity Center" to LatLng(35.3075, -80.7371),
        "Cato Hall" to LatLng(35.3020, -80.7306),
        "Belk Hall" to LatLng(35.3028, -80.7378),
        "Hunt Hall" to LatLng(35.3019, -80.7373),
        "Hawthorn Hall" to LatLng(35.3025, -80.7384),
        "Lynch Hall" to LatLng(35.3033, -80.7388),
        "Witherspoon Hall" to LatLng(35.3036, -80.7368),
        "Miltimore Hall" to LatLng(35.3019, -80.7365),
        "Levine Hall" to LatLng(35.3014, -80.7358),
        "Cone Center" to LatLng(35.3067, -80.7354),
        "Prospector" to LatLng(35.3070, -80.7360),
        "Marriott Hotel" to LatLng(35.3123, -80.7425),
        "Popp Martin Student Union" to LatLng(35.3085, -80.7339),
        "Halton Arena" to LatLng(35.3077, -80.7363),
    )

    val center: LatLng = LatLng(35.3072, -80.7328)
}
