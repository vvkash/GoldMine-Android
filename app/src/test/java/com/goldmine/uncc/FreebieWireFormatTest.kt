package com.goldmine.uncc

import com.goldmine.uncc.data.model.FreebieEvent
import com.goldmine.uncc.data.model.MapLocation
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date
import java.util.UUID

/**
 * The Android and iOS clients share one Firestore collection, so the document shape written here
 * has to stay identical to what the iOS `EnergyDrinkEvent` Codable conformance produces. These
 * tests pin that contract — if a field is renamed, retyped or dropped, they fail.
 */
class FreebieWireFormatTest {

    private val location = MapLocation(
        id = "A1B2C3D4-1111-2222-3333-444455556666",
        title = "Student Union",
        latitude = 35.3088,
        longitude = -80.7346,
    )

    private fun sampleEvent(date: Date = Date(1_700_000_000_000L)) = FreebieEvent(
        id = "11112222-3333-4444-5555-666677778888",
        company = "Celsius",
        location = location,
        votes = 3,
        date = date,
        noVotes = 1,
        isEnded = false,
        hasNotifiedUsers = false,
        votedUserIds = listOf("device-a", "device-b"),
        noVotedUserIds = listOf("device-c"),
    )

    @Test
    fun `event map has exactly the iOS field set`() {
        val expected = setOf(
            "id", "company", "location", "votes", "date",
            "noVotes", "isEnded", "hasNotifiedUsers", "votedUserIds", "noVotedUserIds",
        )
        assertEquals(expected, sampleEvent().toMap().keys)
    }

    @Test
    fun `event map preserves values and Firestore types`() {
        val date = Date(1_700_000_000_000L)
        val map = sampleEvent(date).toMap()

        assertEquals("11112222-3333-4444-5555-666677778888", map["id"])
        assertEquals("Celsius", map["company"])
        assertEquals(3, map["votes"])
        assertEquals(1, map["noVotes"])
        assertEquals(false, map["isEnded"])
        assertEquals(false, map["hasNotifiedUsers"])
        assertEquals(listOf("device-a", "device-b"), map["votedUserIds"])
        assertEquals(listOf("device-c"), map["noVotedUserIds"])

        // Swift encodes `Date` as a Firestore Timestamp, not millis, so iOS decodes it directly.
        val timestamp = map["date"] as Timestamp
        assertEquals(date, timestamp.toDate())
    }

    @Test
    fun `location is written as a nested map, not a GeoPoint`() {
        val nested = sampleEvent().toMap()["location"]
        assertTrue("location must be a nested map for iOS Codable", nested is Map<*, *>)

        val map = nested as Map<*, *>
        assertEquals(setOf("id", "title", "latitude", "longitude"), map.keys)
        assertEquals("Student Union", map["title"])
        assertEquals(35.3088, map["latitude"] as Double, 1e-9)
        assertEquals(-80.7346, map["longitude"] as Double, 1e-9)
    }

    @Test
    fun `location survives a map round trip`() {
        val restored = MapLocation.fromMap(location.toMap())
        assertEquals(location, restored)
    }

    @Test
    fun `location decodes the Int coordinates Firestore may hand back`() {
        val restored = MapLocation.fromMap(
            mapOf("id" to "X", "title" to "Quad", "latitude" to 35, "longitude" to -80),
        )
        assertNotNull(restored)
        assertEquals(35.0, restored!!.latitude, 1e-9)
        assertEquals(-80.0, restored.longitude, 1e-9)
    }

    @Test
    fun `location rejects documents missing required fields`() {
        assertNull(MapLocation.fromMap(null))
        assertNull(MapLocation.fromMap(mapOf("latitude" to 1.0, "longitude" to 2.0)))
        assertNull(MapLocation.fromMap(mapOf("title" to "No coords")))
    }

    @Test
    fun `generated ids are uppercase UUID strings like Swift's uuidString`() {
        repeat(20) {
            val id = FreebieEvent(company = "c", location = location, votes = 0, date = Date()).id
            assertEquals(id.uppercase(), id)
            // Throws if the value is not a well-formed UUID.
            assertEquals(id, UUID.fromString(id).toString().uppercase())
        }
    }

    @Test
    fun `vote threshold matches the Cloud Function trigger`() {
        assertEquals(2, FreebieEvent.VOTE_THRESHOLD)
    }

    @Test
    fun `newly created events do not claim to have notified users`() {
        // iOS sets hasNotifiedUsers eagerly, which permanently blocks the Cloud Function's
        // `!afterData.hasNotifiedUsers` guard. Android must leave it false so push fan-out fires.
        val event = FreebieEvent(company = "Red Bull", location = location, votes = 1, date = Date())
        assertEquals(false, event.hasNotifiedUsers)
        assertEquals(false, event.toMap()["hasNotifiedUsers"])
    }
}
