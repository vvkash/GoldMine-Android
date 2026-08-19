package com.goldmine.uncc

import com.goldmine.uncc.core.formatMinutesOfDay
import com.goldmine.uncc.data.model.CampusBuildings
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.Weekday
import com.goldmine.uncc.data.model.classesOn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/** Covers the class schedule model that replaces the iOS `ClassManager`. */
class ClassScheduleTest {

    private fun classItem(
        name: String,
        days: Set<Weekday>,
        start: Int = 9 * 60,
        end: Int = 10 * 60,
        building: String = "Woodward",
    ) = ClassItem(
        name = name,
        buildingName = building,
        roomNumber = "130",
        startMinutes = start,
        endMinutes = end,
        days = days,
        color = "#005035",
    )

    @Test
    fun `classesOn returns only the classes scheduled that weekday`() {
        val mwf = classItem("Data Structures", setOf(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY))
        val tth = classItem("Calculus", setOf(Weekday.TUESDAY, Weekday.THURSDAY))
        val schedule = listOf(mwf, tth)

        // 2024-01-08 is a Monday.
        assertEquals(listOf(mwf), schedule.classesOn(LocalDate.of(2024, 1, 8)))
        assertEquals(listOf(tth), schedule.classesOn(LocalDate.of(2024, 1, 9)))
        assertEquals(listOf(mwf), schedule.classesOn(LocalDate.of(2024, 1, 10)))
        assertEquals(emptyList<ClassItem>(), schedule.classesOn(LocalDate.of(2024, 1, 13)))
    }

    @Test
    fun `classesOn preserves the original ordering`() {
        val a = classItem("A", setOf(Weekday.MONDAY), start = 15 * 60)
        val b = classItem("B", setOf(Weekday.MONDAY), start = 8 * 60)
        assertEquals(
            listOf("A", "B"),
            listOf(a, b).classesOn(LocalDate.of(2024, 1, 8)).map { it.name },
        )
    }

    @Test
    fun `Weekday maps every java time DayOfWeek`() {
        assertEquals(Weekday.MONDAY, Weekday.from(DayOfWeek.MONDAY))
        assertEquals(Weekday.SUNDAY, Weekday.from(DayOfWeek.SUNDAY))
        DayOfWeek.entries.forEach { assertNotNull(Weekday.from(it)) }
    }

    @Test
    fun `Weekday raw values match the Swift enum`() {
        assertEquals(
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            Weekday.entries.map { it.rawValue },
        )
        assertEquals(Weekday.THURSDAY, Weekday.fromRaw("Thursday"))
        assertNull(Weekday.fromRaw("Thur"))
    }

    @Test
    fun `Weekday short names are three letters`() {
        assertEquals("Mon", Weekday.MONDAY.shortName)
        assertEquals("Sun", Weekday.SUNDAY.shortName)
        Weekday.entries.forEach { assertEquals(3, it.shortName.length) }
    }

    @Test
    fun `minutes of day format as a 12 hour clock`() {
        assertEquals("12:00 AM", formatMinutesOfDay(0))
        assertEquals("9:00 AM", formatMinutesOfDay(9 * 60))
        assertEquals("12:30 PM", formatMinutesOfDay(12 * 60 + 30))
        assertEquals("11:59 PM", formatMinutesOfDay(23 * 60 + 59))
    }

    @Test
    fun `minutes of day wrap instead of throwing`() {
        assertEquals("12:00 AM", formatMinutesOfDay(24 * 60))
        assertEquals("1:00 AM", formatMinutesOfDay(25 * 60))
        assertEquals("11:00 PM", formatMinutesOfDay(-60))
    }

    @Test
    fun `every campus building resolves to a coordinate on campus`() {
        assertTrue(CampusBuildings.buildings.isNotEmpty())
        CampusBuildings.buildings.forEach { (name, latLng) ->
            assertTrue("$name latitude out of range", latLng.latitude in 35.29..35.32)
            assertTrue("$name longitude out of range", latLng.longitude in -80.75..-80.72)
        }
    }

    @Test
    fun `building names are sorted and complete`() {
        assertEquals(CampusBuildings.buildings.keys.sorted(), CampusBuildings.names)
        assertNotNull(CampusBuildings.coordinateFor("Atkins Library"))
        assertNull(CampusBuildings.coordinateFor("Not A Building"))
    }
}
