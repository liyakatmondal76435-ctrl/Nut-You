package com.example.ui.model

import java.util.concurrent.TimeUnit

data class StreakTime(
    val totalMillis: Long = 0L,
    val days: Long = 0L,
    val hours: Long = 0L,
    val minutes: Long = 0L,
    val seconds: Long = 0L
) {
    companion object {
        fun fromDurationMillis(durationMillis: Long): StreakTime {
            val clamped = durationMillis.coerceAtLeast(0L)
            val days = TimeUnit.MILLISECONDS.toDays(clamped)
            val hours = TimeUnit.MILLISECONDS.toHours(clamped) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(clamped) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(clamped) % 60
            return StreakTime(
                totalMillis = clamped,
                days = days,
                hours = hours,
                minutes = minutes,
                seconds = seconds
            )
        }
    }

    fun formattedSummary(): String {
        return when {
            days > 0 -> "$days d, $hours h, $minutes m"
            hours > 0 -> "$hours h, $minutes m, $seconds s"
            else -> "$minutes m, $seconds s"
        }
    }
}
