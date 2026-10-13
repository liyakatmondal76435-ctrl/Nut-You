package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyCheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {
    @Query("SELECT * FROM daily_checkins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<DailyCheckIn>>

    @Query("SELECT * FROM daily_checkins WHERE dateKey = :dateKey LIMIT 1")
    fun getCheckInByDate(dateKey: String): Flow<DailyCheckIn?>

    @Query("SELECT COUNT(DISTINCT dateKey) FROM daily_checkins")
    fun getTotalCheckInDays(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: DailyCheckIn)

    @Query("DELETE FROM daily_checkins WHERE id = :id")
    suspend fun deleteCheckIn(id: Long)

    @Query("DELETE FROM daily_checkins")
    suspend fun deleteAllCheckIns()
}
