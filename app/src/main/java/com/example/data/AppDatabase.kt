package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.DailyCheckInDao
import com.example.data.dao.HabitDao
import com.example.data.dao.RelapseLogDao
import com.example.data.dao.UrgeLogDao
import com.example.data.model.DailyCheckIn
import com.example.data.model.HabitProfile
import com.example.data.model.RelapseLog
import com.example.data.model.UrgeLog

@Database(
    entities = [
        HabitProfile::class,
        DailyCheckIn::class,
        RelapseLog::class,
        UrgeLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun relapseLogDao(): RelapseLogDao
    abstract fun urgeLogDao(): UrgeLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clean_streak_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
