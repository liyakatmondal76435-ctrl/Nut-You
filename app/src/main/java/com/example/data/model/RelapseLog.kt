package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relapse_logs")
data class RelapseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val streakDurationMillis: Long,
    val triggers: String, // Comma-separated triggers: "Stress, Late Night, Boredom"
    val note: String = "",
    val lessonLearned: String = ""
)
