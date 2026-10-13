package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_checkins")
data class DailyCheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val dateKey: String, // format "YYYY-MM-DD"
    val timestamp: Long = System.currentTimeMillis(),
    val moodRating: Int, // 1 to 5 (1: Heavy, 2: Uneasy, 3: Calm, 4: Confident, 5: Triumphant)
    val urgeIntensity: Int, // 0: None, 1: Mild, 2: Moderate, 3: Severe
    val note: String = "",
    val gratitude: String = ""
)
