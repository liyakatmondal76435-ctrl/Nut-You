package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UrgeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface UrgeLogDao {
    @Query("SELECT * FROM urge_logs ORDER BY timestamp DESC")
    fun getAllUrgeLogs(): Flow<List<UrgeLog>>

    @Query("SELECT COUNT(*) FROM urge_logs WHERE wasOvercome = 1")
    fun getOvercomeUrgesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrgeLog(log: UrgeLog): Long

    @Query("DELETE FROM urge_logs WHERE id = :id")
    suspend fun deleteUrgeLog(id: Long)

    @Query("DELETE FROM urge_logs")
    suspend fun deleteAllUrgeLogs()
}
