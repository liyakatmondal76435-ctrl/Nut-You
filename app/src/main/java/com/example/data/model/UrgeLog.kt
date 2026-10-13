package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urge_logs")
data class UrgeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val trigger: String = "Spontaneous Urge",
    val intensity: Int = 5, // 1 to 10 scale
    val copingTool: String = "Breathing Exercise", // e.g., "Box Breathing", "Urge Surfing", "Cold Water", "Pushups"
    val wasOvercome: Boolean = true,
    val reflectionNote: String = ""
)
