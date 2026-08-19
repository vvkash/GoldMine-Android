package com.goldmine.uncc

import com.goldmine.uncc.data.remote.GymOccupancyCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/** Pins the UREC occupancy curves and operating hours to the iOS `GymOccupancyService` values. */
class GymOccupancyCalculatorTest {

    private fun at(y: Int, m: Int, d: Int, hour: Int) = LocalDateTime.of(y, m, d, hour, 0)

    // 2024-01-08 is a Monday, 2024-01-13 a Saturday, 2024-01-14 a Sunday.
    private fun monday(hour: Int) = at(2024, 1, 8, hour)
    private fun saturday(hour: Int) = at(2024, 1, 13, hour)
    private fun sunday(hour: Int) = at(2024, 1, 14, hour)

    @Test
    fun `weekday hours run 6am to 10pm`() {
        assertTrue(GymOccupancyCalculator.occupancyAt(monday(5)).isClosed)
        assertFalse(GymOccupancyCalculator.occupancyAt(monday(6)).isClosed)
        assertFalse(GymOccupancyCalculator.occupancyAt(monday(21)).isClosed)
        assertTrue(GymOccupancyCalculator.occupancyAt(monday(22)).isClosed)
    }

    @Test
    fun `weekend hours run 9am to 8pm`() {
        assertTrue(GymOccupancyCalculator.occupancyAt(saturday(8)).isClosed)
        assertFalse(GymOccupancyCalculator.occupancyAt(saturday(9)).isClosed)
        assertFalse(GymOccupancyCalculator.occupancyAt(saturday(19)).isClosed)
        assertTrue(GymOccupancyCalculator.occupancyAt(saturday(20)).isClosed)

        assertTrue(GymOccupancyCalculator.occupancyAt(sunday(8)).isClosed)
        assertFalse(GymOccupancyCalculator.occupancyAt(sunday(9)).isClosed)
        assertTrue(GymOccupancyCalculator.occupancyAt(sunday(20)).isClosed)
    }

    @Test
    fun `closed hours report zero occupancy`() {
        val closed = GymOccupancyCalculator.occupancyAt(monday(3))
        assertTrue(closed.isClosed)
        assertEquals(0, closed.currentOccupancy)
    }

    @Test
    fun `weekday peak is the 6pm slot`() {
        assertEquals(95, GymOccupancyCalculator.occupancyAt(monday(18)).currentOccupancy)
        assertEquals(85, GymOccupancyCalculator.occupancyAt(monday(17)).currentOccupancy)
        assertEquals(45, GymOccupancyCalculator.occupancyAt(monday(7)).currentOccupancy)
    }

    @Test
    fun `every weekday shares Mondays curve`() {
        // 2024-01-08 Mon .. 2024-01-12 Fri
        val values = (8..12).map {
            GymOccupancyCalculator.occupancyAt(at(2024, 1, it, 18)).currentOccupancy
        }
        assertEquals(listOf(95, 95, 95, 95, 95), values)
    }

    @Test
    fun `saturday and sunday use their own curves`() {
        assertEquals(50, GymOccupancyCalculator.occupancyAt(saturday(13)).currentOccupancy)
        assertEquals(45, GymOccupancyCalculator.occupancyAt(sunday(13)).currentOccupancy)
    }

    @Test
    fun `capacity is a percentage scale`() {
        val occupancy = GymOccupancyCalculator.occupancyAt(monday(18))
        assertEquals(100, occupancy.maxCapacity)
        assertEquals(18, occupancy.hourOfDay)
    }

    @Test
    fun `todays curve is sorted and covers opening hours`() {
        val weekday = GymOccupancyCalculator.todaysCurve(monday(12))
        assertEquals(17, weekday.size)
        assertEquals(6, weekday.first().first)
        assertEquals(22, weekday.last().first)
        assertEquals(weekday.map { it.first }.sorted(), weekday.map { it.first })

        val weekend = GymOccupancyCalculator.todaysCurve(saturday(12))
        assertEquals(12, weekend.size)
        assertEquals(9, weekend.first().first)
        assertEquals(20, weekend.last().first)
    }
}
