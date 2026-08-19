package com.goldmine.uncc

import com.goldmine.uncc.core.colorFromHex
import com.goldmine.uncc.core.toHexString
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.MapLocation
import com.goldmine.uncc.data.model.Weekday
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Class schedules and colours are persisted as JSON in DataStore. A serialization regression
 * silently wipes a user's saved classes on upgrade, so the round trips are pinned here.
 */
class PersistenceRoundTripTest {

    private val json = Json { ignoreUnknownKeys = true }

    private val sample = ClassItem(
        id = "fixed-id",
        name = "Data Structures",
        buildingName = "Woodward",
        roomNumber = "130",
        startMinutes = 9 * 60,
        endMinutes = 10 * 60 + 15,
        days = setOf(Weekday.MONDAY, Weekday.WEDNESDAY),
        color = "#005035",
    )

    @Test
    fun `class item survives a JSON round trip`() {
        val restored = json.decodeFromString<ClassItem>(json.encodeToString(sample))
        assertEquals(sample, restored)
    }

    @Test
    fun `class list survives a JSON round trip`() {
        val list = listOf(sample, sample.copy(id = "second", name = "Calculus"))
        val restored = json.decodeFromString<List<ClassItem>>(json.encodeToString(list))
        assertEquals(list, restored)
    }

    @Test
    fun `weekdays are stored by name so the payload stays readable and stable`() {
        val encoded = json.encodeToString(sample)
        assertTrue(encoded, encoded.contains("MONDAY"))
        assertTrue(encoded, encoded.contains("WEDNESDAY"))
    }

    @Test
    fun `unknown fields from a newer build are ignored rather than crashing`() {
        val payload = """
            {"id":"x","name":"N","buildingName":"B","roomNumber":"1","startMinutes":60,
             "endMinutes":120,"days":["FRIDAY"],"color":"#FFFFFF","futureField":42}
        """.trimIndent()
        val restored = json.decodeFromString<ClassItem>(payload)
        assertEquals(setOf(Weekday.FRIDAY), restored.days)
    }

    @Test
    fun `map location survives a JSON round trip`() {
        val location = MapLocation(
            id = "ABC",
            title = "Student Union",
            latitude = 35.3088,
            longitude = -80.7346,
        )
        assertEquals(location, json.decodeFromString<MapLocation>(json.encodeToString(location)))
    }

    @Test
    fun `hex colours round trip`() {
        listOf("#005035", "#A49665", "#FFFFFF", "#000000", "#FF0000").forEach { hex ->
            assertEquals(hex, colorFromHex(hex).toHexString())
        }
    }

    @Test
    fun `hex parsing accepts values with and without the leading hash`() {
        assertEquals(colorFromHex("#005035"), colorFromHex("005035"))
        assertEquals(colorFromHex("#005035"), colorFromHex("  005035  "))
    }

    @Test
    fun `malformed hex falls back to grey instead of throwing`() {
        assertEquals(androidx.compose.ui.graphics.Color.Gray, colorFromHex("not-a-colour"))
        assertEquals(androidx.compose.ui.graphics.Color.Gray, colorFromHex(""))
    }
}
