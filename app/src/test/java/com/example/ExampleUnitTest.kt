package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testManualServiceCalculationBasic() {
        val periods = listOf(
            ServicePeriod(years = "5", months = "8", days = "15"),
            ServicePeriod(years = "2", months = "6", days = "20")
        )
        val result = ServiceCalculatorHelper.calculateManualService(periods)

        // 5y 8m 15d + 2y 6m 20d:
        // Days: 15 + 20 = 35 -> 1 month, 5 days
        // Months: 8 + 6 + 1 = 15 -> 1 year, 3 months
        // Years: 5 + 2 + 1 = 8 years
        assertEquals(8, result.years)
        assertEquals(3, result.months)
        assertEquals(5, result.days)
    }

    @Test
    fun testDoubleServiceMultiplier() {
        val periods = listOf(
            ServicePeriod(years = "2", months = "3", days = "0", isDoubleService = true)
        )
        val result = ServiceCalculatorHelper.calculateManualService(periods)
        // 2y 3m * 2 = 4y 6m
        assertEquals(4, result.years)
        assertEquals(6, result.months)
    }

    @Test
    fun testKurdishDigitConversion() {
        val formatted = ServiceCalculatorHelper.formatNumber(2024, true)
        assertEquals("٢٠٢٤", formatted)

        val cleaned = ServiceCalculatorHelper.cleanDigits("١٤")
        assertEquals("14", cleaned)
    }
}
