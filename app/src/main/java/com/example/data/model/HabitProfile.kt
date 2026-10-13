package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_profile")
data class HabitProfile(
    @PrimaryKey val id: Int = 1,
    val habitName: String = "Clean Streak",
    val pledge: String = "I choose clarity, resilience, and presence today.",
    val currentStreakStartTime: Long = System.currentTimeMillis(),
    val longestStreakMillis: Long = 0L,
    val isAppLockEnabled: Boolean = false,
    val pinCode: String = "",
    val urgesDefeatedCount: Int = 0,
    val totalCleanDaysCached: Int = 0
)
