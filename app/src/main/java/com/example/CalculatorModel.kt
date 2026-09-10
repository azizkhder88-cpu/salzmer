package com.example

import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Period model representing a single tenure/service segment entered manually.
 */
data class ServicePeriod(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "",
    val years: String = "",
    val months: String = "",
    val days: String = "",
    val isDoubleService: Boolean = false, // خزمەتی دووقات (2x)
    val isDeduction: Boolean = false       // لێدەرکردن (مۆڵەتی بێ مووچە یان سڕینەوە)
)

/**
 * Result model representing computed tenure.
 */
data class CalculationResult(
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val totalMonths: Int = 0,
    val totalDays: Long = 0,
    val decimalYears: Double = 0.0,
    val summaryText: String = ""
)

/**
 * History item for saved calculations.
 */
data class CalculationRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val years: Int,
    val months: Int,
    val days: Int,
    val details: String
)

object ServiceCalculatorHelper {

    /**
     * Converts western digits to Kurdish/Eastern Arabic digits if requested
     */
    fun formatNumber(value: Any, kurdishDigits: Boolean): String {
        val str = value.toString()
        if (!kurdishDigits) return str
        val easternNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(easternNumerals[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Eastern Arabic digits (٠-٩) and Persian digits back to standard digits for parsing
     */
    fun cleanDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            when (ch) {
                '٠', '۰' -> sb.append('0')
                '١', '۱' -> sb.append('1')
                '٢', '۲' -> sb.append('2')
                '٣', '۳' -> sb.append('3')
                '٤', '۴' -> sb.append('4')
                '٥', '۵' -> sb.append('5')
                '٦', '۶' -> sb.append('6')
                '٧', '۷' -> sb.append('7')
                '٨', '۸' -> sb.append('8')
                '٩', '۹' -> sb.append('9')
                else -> if (ch.isDigit()) sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Calculates combined service duration from multiple manually entered periods.
     * Standard military/administrative convention: 1 month = 30 days, 1 year = 12 months = 360 days.
     */
    fun calculateManualService(periods: List<ServicePeriod>): CalculationResult {
        var totalNetDays: Long = 0

        for (period in periods) {
            val y = cleanDigits(period.years).toIntOrNull() ?: 0
            val m = cleanDigits(period.months).toIntOrNull() ?: 0
            val d = cleanDigits(period.days).toIntOrNull() ?: 0

            // Base days in standard administrative accounting (1 year = 360 days, 1 month = 30 days)
            val periodDays = (y * 360L) + (m * 30L) + d
            val multiplier = if (period.isDoubleService) 2 else 1
            val computedDays = periodDays * multiplier

            if (period.isDeduction) {
                totalNetDays -= computedDays
            } else {
                totalNetDays += computedDays
            }
        }

        if (totalNetDays <= 0) {
            return CalculationResult()
        }

        val calculatedYears = (totalNetDays / 360L).toInt()
        val remainingAfterYears = totalNetDays % 360L
        val calculatedMonths = (remainingAfterYears / 30L).toInt()
        val calculatedDays = (remainingAfterYears % 30L).toInt()

        val totalMonths = (calculatedYears * 12) + calculatedMonths
        val decimalYears = calculatedYears + (calculatedMonths / 12.0) + (calculatedDays / 360.0)

        val summary = "$calculatedYears ساڵ و $calculatedMonths مانگ و $calculatedDays ڕۆژ"

        return CalculationResult(
            years = calculatedYears,
            months = calculatedMonths,
            days = calculatedDays,
            totalMonths = totalMonths,
            totalDays = totalNetDays,
            decimalYears = (Math.round(decimalYears * 100.0) / 100.0),
            summaryText = summary
        )
    }

    /**
     * Calculates tenure between two dates (Start Date and End Date).
     * Uses Calendar for 100% Android compatibility without API level restrictions.
     */
    fun calculateBetweenDates(
        startYear: Int, startMonth: Int, startDay: Int,
        endYear: Int, endMonth: Int, endDay: Int,
        isDoubleService: Boolean = false
    ): CalculationResult {
        return try {
            val startCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, startYear)
                set(Calendar.MONTH, (startMonth - 1).coerceIn(0, 11))
                set(Calendar.DAY_OF_MONTH, startDay.coerceIn(1, 31))
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val endCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, endYear)
                set(Calendar.MONTH, (endMonth - 1).coerceIn(0, 11))
                set(Calendar.DAY_OF_MONTH, endDay.coerceIn(1, 31))
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val (early, late) = if (startCal.after(endCal)) Pair(endCal, startCal) else Pair(startCal, endCal)
            val diffMillis = late.timeInMillis - early.timeInMillis
            val totalDaysBetween = TimeUnit.MILLISECONDS.toDays(diffMillis)

            var y = late.get(Calendar.YEAR) - early.get(Calendar.YEAR)
            var m = late.get(Calendar.MONTH) - early.get(Calendar.MONTH)
            var d = late.get(Calendar.DAY_OF_MONTH) - early.get(Calendar.DAY_OF_MONTH)

            if (d < 0) {
                m -= 1
                d += 30
            }
            if (m < 0) {
                y -= 1
                m += 12
            }
            if (y < 0) y = 0

            if (isDoubleService) {
                var doubleDays = (y * 360L + m * 30L + d) * 2
                y = (doubleDays / 360L).toInt()
                val rem = doubleDays % 360L
                m = (rem / 30L).toInt()
                d = (rem % 30L).toInt()
            }

            val totalMonths = (y * 12) + m
            val decYears = y + (m / 12.0) + (d / 360.0)

            CalculationResult(
                years = y,
                months = m,
                days = d,
                totalMonths = totalMonths,
                totalDays = if (isDoubleService) totalDaysBetween * 2 else totalDaysBetween,
                decimalYears = (Math.round(decYears * 100.0) / 100.0),
                summaryText = "$y ساڵ و $m مانگ و $d ڕۆژ"
            )
        } catch (e: Exception) {
            CalculationResult()
        }
    }

    /**
     * Calculates remaining time for retirement.
     * Target years: e.g. 30 years (military standard) or 25 years.
     */
    fun calculateRetirementRemaining(
        currentYears: Int,
        currentMonths: Int,
        currentDays: Int,
        targetYears: Int
    ): Triple<Int, Int, Int> {
        val totalCurrentDays = (currentYears * 360L) + (currentMonths * 30L) + currentDays
        val targetDays = targetYears * 360L
        val diffDays = targetDays - totalCurrentDays

        if (diffDays <= 0) {
            return Triple(0, 0, 0)
        }

        val remY = (diffDays / 360L).toInt()
        val remAfterY = diffDays % 360L
        val remM = (remAfterY / 30L).toInt()
        val remD = (remAfterY % 30L).toInt()

        return Triple(remY, remM, remD)
    }
}
