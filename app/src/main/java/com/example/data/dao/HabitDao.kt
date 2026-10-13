package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HabitProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_profile WHERE id = 1 LIMIT 1")
    fun getHabitProfile(): Flow<HabitProfile?>

    @Query("SELECT * FROM habit_profile WHERE id = 1 LIMIT 1")
    suspend fun getHabitProfileSync(): HabitProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: HabitProfile)

    @Update
    suspend fun update(profile: HabitProfile)

    @Query("UPDATE habit_profile SET currentStreakStartTime = :startTime WHERE id = 1")
    suspend fun updateStreakStartTime(startTime: Long)

    @Query("UPDATE habit_profile SET urgesDefeatedCount = urgesDefeatedCount + 1 WHERE id = 1")
    suspend fun incrementUrgesDefeated()

    @Query("UPDATE habit_profile SET longestStreakMillis = :longest WHERE id = 1")
    suspend fun updateLongestStreak(longest: Long)
}
