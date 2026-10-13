package com.example.data

import com.example.data.dao.DailyCheckInDao
import com.example.data.dao.HabitDao
import com.example.data.dao.RelapseLogDao
import com.example.data.dao.UrgeLogDao
import com.example.data.model.DailyCheckIn
import com.example.data.model.HabitProfile
import com.example.data.model.RelapseLog
import com.example.data.model.UrgeLog
import kotlinx.coroutines.flow.Flow

class CleanStreakRepository(
    private val habitDao: HabitDao,
    private val dailyCheckInDao: DailyCheckInDao,
    private val relapseLogDao: RelapseLogDao,
    private val urgeLogDao: UrgeLogDao
) {
    val habitProfile: Flow<HabitProfile?> = habitDao.getHabitProfile()
    val allCheckIns: Flow<List<DailyCheckIn>> = dailyCheckInDao.getAllCheckIns()
    val totalCheckInDays: Flow<Int> = dailyCheckInDao.getTotalCheckInDays()
    val allRelapseLogs: Flow<List<RelapseLog>> = relapseLogDao.getAllRelapseLogs()
    val allUrgeLogs: Flow<List<UrgeLog>> = urgeLogDao.getAllUrgeLogs()
    val overcomeUrgesCount: Flow<Int> = urgeLogDao.getOvercomeUrgesCount()

    fun getCheckInForDate(dateKey: String): Flow<DailyCheckIn?> =
        dailyCheckInDao.getCheckInByDate(dateKey)

    suspend fun ensureProfileExists(): HabitProfile {
        val existing = habitDao.getHabitProfileSync()
        if (existing == null) {
            val defaultProfile = HabitProfile(
                id = 1,
                habitName = "Clean Streak",
                pledge = "I choose clarity, resilience, and presence today.",
                currentStreakStartTime = System.currentTimeMillis(),
                longestStreakMillis = 0L,
                isAppLockEnabled = false,
                pinCode = "",
                urgesDefeatedCount = 0
            )
            habitDao.insertOrUpdate(defaultProfile)
            return defaultProfile
        }
        return existing
    }

    suspend fun updateHabitProfile(profile: HabitProfile) {
        habitDao.insertOrUpdate(profile)
    }

    suspend fun resetStreak(
        triggers: String,
        note: String,
        lesson: String
    ) {
        val profile = ensureProfileExists()
        val now = System.currentTimeMillis()
        val streakDuration = (now - profile.currentStreakStartTime).coerceAtLeast(0L)

        // Save relapse log
        val relapse = RelapseLog(
            timestamp = now,
            streakDurationMillis = streakDuration,
            triggers = triggers,
            note = note,
            lessonLearned = lesson
        )
        relapseLogDao.insertRelapseLog(relapse)

        // Update longest streak if previous streak was greater
        val newLongest = maxOf(profile.longestStreakMillis, streakDuration)

        // Reset current streak to now
        habitDao.insertOrUpdate(
            profile.copy(
                currentStreakStartTime = now,
                longestStreakMillis = newLongest
            )
        )
    }

    suspend fun recordCheckIn(checkIn: DailyCheckIn) {
        dailyCheckInDao.insertCheckIn(checkIn)
    }

    suspend fun recordUrgeLog(urgeLog: UrgeLog) {
        urgeLogDao.insertUrgeLog(urgeLog)
        if (urgeLog.wasOvercome) {
            habitDao.incrementUrgesDefeated()
        }
    }

    suspend fun deleteRelapseLog(id: Long) {
        relapseLogDao.deleteRelapseLog(id)
    }

    suspend fun deleteCheckIn(id: Long) {
        dailyCheckInDao.deleteCheckIn(id)
    }

    suspend fun deleteUrgeLog(id: Long) {
        urgeLogDao.deleteUrgeLog(id)
    }

    suspend fun resetAllData() {
        val now = System.currentTimeMillis()
        dailyCheckInDao.deleteAllCheckIns()
        relapseLogDao.deleteAllRelapseLogs()
        urgeLogDao.deleteAllUrgeLogs()
        habitDao.insertOrUpdate(
            HabitProfile(
                id = 1,
                habitName = "Clean Streak",
                pledge = "I choose clarity, resilience, and presence today.",
                currentStreakStartTime = now,
                longestStreakMillis = 0L,
                isAppLockEnabled = false,
                pinCode = "",
                urgesDefeatedCount = 0
            )
        )
    }
}
