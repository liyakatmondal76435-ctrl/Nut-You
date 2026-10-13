package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RelapseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseLogDao {
    @Query("SELECT * FROM relapse_logs ORDER BY timestamp DESC")
    fun getAllRelapseLogs(): Flow<List<RelapseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelapseLog(log: RelapseLog): Long

    @Query("DELETE FROM relapse_logs WHERE id = :id")
    suspend fun deleteRelapseLog(id: Long)

    @Query("DELETE FROM relapse_logs")
    suspend fun deleteAllRelapseLogs()
}
