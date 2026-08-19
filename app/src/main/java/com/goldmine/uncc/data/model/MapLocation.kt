package com.goldmine.uncc.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A named coordinate. Field names match the iOS `MapLocation` Codable representation so the
 * nested Firestore map written by either platform decodes on the other.
 */
@Serializable
data class MapLocation(
    val id: String = UUID.randomUUID().toString().uppercase(),
    val title: String,
    val latitude: Double,
    val longitude: Double,
) {
    val latLng: LatLng get() = LatLng(latitude, longitude)

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "latitude" to latitude,
        "longitude" to longitude,
    )

    companion object {
        fun fromMap(raw: Map<*, *>?): MapLocation? {
            if (raw == null) return null
            val title = raw["title"] as? String ?: return null
            val latitude = (raw["latitude"] as? Number)?.toDouble() ?: return null
            val longitude = (raw["longitude"] as? Number)?.toDouble() ?: return null
            return MapLocation(
                id = raw["id"] as? String ?: UUID.randomUUID().toString().uppercase(),
                title = title,
                latitude = latitude,
                longitude = longitude,
            )
        }
    }
}
