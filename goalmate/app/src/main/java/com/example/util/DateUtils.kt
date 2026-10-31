package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val standardDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val displayFullDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun todayDateString(): String {
        return standardDateFormat.format(Date())
    }

    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatFullDate(timestamp: Long = System.currentTimeMillis()): String {
        return displayFullDateFormat.format(Date(timestamp))
    }

    fun formatMonthYear(calendar: Calendar): String {
        return monthYearFormat.format(calendar.time)
    }

    fun toDateString(timestamp: Long): String {
        return standardDateFormat.format(Date(timestamp))
    }

    fun parseDateStringToMillis(dateStr: String): Long {
        return runCatching {
            standardDateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
        }.getOrDefault(System.currentTimeMillis())
    }

    fun getDaysRemaining(deadlineMillis: Long): Int {
        val diff = deadlineMillis - System.currentTimeMillis()
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getDaysRemainingText(deadlineMillis: Long): String {
        val days = getDaysRemaining(deadlineMillis)
        return when {
            days < 0 -> "${-days}d overdue"
            days == 0 -> "Due today"
            days == 1 -> "1 day left"
            else -> "$days days left"
        }
    }

    /**
     * Returns list of dates (as YYYY-MM-DD strings) for the last [count] days including today.
     */
    fun getLastNDays(count: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(count - 1))
        for (i in 0 until count) {
            list.add(standardDateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun getDayOfWeekAbbrev(dateStr: String): String {
        return runCatching {
            val date = standardDateFormat.parse(dateStr) ?: return ""
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            sdf.format(date).take(3)
        }.getOrDefault("")
    }

    fun getDayOfMonth(dateStr: String): Int {
        return runCatching {
            val date = standardDateFormat.parse(dateStr) ?: return 1
            val cal = Calendar.getInstance()
            cal.time = date
            cal.get(Calendar.DAY_OF_MONTH)
        }.getOrDefault(1)
    }

    fun offsetDate(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return standardDateFormat.format(cal.time)
    }
}
