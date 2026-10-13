package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CleanStreakRepository
import com.example.data.model.DailyCheckIn
import com.example.data.model.HabitProfile
import com.example.data.model.RelapseLog
import com.example.data.model.UrgeLog
import com.example.ui.model.MilestoneProgress
import com.example.ui.model.Milestones
import com.example.ui.model.Quote
import com.example.ui.model.QuotesAndWisdom
import com.example.ui.model.StreakTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class CleanStreakUiState(
    val habitProfile: HabitProfile = HabitProfile(),
    val streakTime: StreakTime = StreakTime(),
    val longestStreakTime: StreakTime = StreakTime(),
    val milestones: List<MilestoneProgress> = emptyList(),
    val nextMilestone: MilestoneProgress? = null,
    val todayCheckIn: DailyCheckIn? = null,
    val allCheckIns: List<DailyCheckIn> = emptyList(),
    val relapseLogs: List<RelapseLog> = emptyList(),
    val urgeLogs: List<UrgeLog> = emptyList(),
    val totalCheckInDays: Int = 0,
    val overcomeUrgesCount: Int = 0,
    val isAppLocked: Boolean = false,
    val themeMode: String = "system", // "system", "dark", "light"
    val dailyQuote: Quote = QuotesAndWisdom.DAILY_QUOTES.first(),
    val isPledgedToday: Boolean = false
)

class CleanStreakViewModel(
    private val repository: CleanStreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CleanStreakUiState())
    val uiState: StateFlow<CleanStreakUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayKey: String
        get() = dateFormat.format(Date())

    init {
        viewModelScope.launch {
            repository.ensureProfileExists()
        }

        // Combine reactive DB flows
        combine(
            repository.habitProfile,
            repository.allCheckIns,
            repository.allRelapseLogs,
            repository.allUrgeLogs
        ) { profile, checkIns, relapses, urges ->
            val nonNullProfile = profile ?: HabitProfile()
            val todayRecord = checkIns.find { it.dateKey == todayKey }
            val now = System.currentTimeMillis()
            val currentDuration = (now - nonNullProfile.currentStreakStartTime).coerceAtLeast(0L)
            val longestDuration = maxOf(nonNullProfile.longestStreakMillis, currentDuration)

            val calculatedMilestones = calculateMilestones(currentDuration)
            val nextMilestone = calculatedMilestones.firstOrNull { !it.isUnlocked }

            _uiState.value = _uiState.value.copy(
                habitProfile = nonNullProfile,
                streakTime = StreakTime.fromDurationMillis(currentDuration),
                longestStreakTime = StreakTime.fromDurationMillis(longestDuration),
                milestones = calculatedMilestones,
                nextMilestone = nextMilestone,
                todayCheckIn = todayRecord,
                allCheckIns = checkIns,
                relapseLogs = relapses,
                urgeLogs = urges,
                totalCheckInDays = checkIns.map { it.dateKey }.distinct().size,
                overcomeUrgesCount = nonNullProfile.urgesDefeatedCount + urges.count { it.wasOvercome },
                isAppLocked = nonNullProfile.isAppLockEnabled && nonNullProfile.pinCode.isNotEmpty()
            )
        }.launchIn(viewModelScope)

        // Select random daily quote based on day of year
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val quote = QuotesAndWisdom.DAILY_QUOTES[dayOfYear % QuotesAndWisdom.DAILY_QUOTES.size]
        _uiState.value = _uiState.value.copy(dailyQuote = quote)

        // Start live ticker coroutine
        startLiveTicker()
    }

    private fun startLiveTicker() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentProfile = _uiState.value.habitProfile
                val now = System.currentTimeMillis()
                val currentDuration = (now - currentProfile.currentStreakStartTime).coerceAtLeast(0L)
                val longestDuration = maxOf(currentProfile.longestStreakMillis, currentDuration)
                val calculatedMilestones = calculateMilestones(currentDuration)
                val nextMilestone = calculatedMilestones.firstOrNull { !it.isUnlocked }

                _uiState.value = _uiState.value.copy(
                    streakTime = StreakTime.fromDurationMillis(currentDuration),
                    longestStreakTime = StreakTime.fromDurationMillis(longestDuration),
                    milestones = calculatedMilestones,
                    nextMilestone = nextMilestone
                )
            }
        }
    }

    private fun calculateMilestones(streakDurationMillis: Long): List<MilestoneProgress> {
        return Milestones.ALL.map { milestone ->
            val isUnlocked = streakDurationMillis >= milestone.requiredDurationMillis
            val progress = (streakDurationMillis.toFloat() / milestone.requiredDurationMillis).coerceIn(0f, 1f)
            val remainingMillis = (milestone.requiredDurationMillis - streakDurationMillis).coerceAtLeast(0L)
            val remainingFormatted = formatRemainingTime(remainingMillis)

            MilestoneProgress(
                milestone = milestone,
                isUnlocked = isUnlocked,
                progress = progress,
                remainingFormatted = remainingFormatted
            )
        }
    }

    private fun formatRemainingTime(remainingMillis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
        return when {
            days > 0 -> "$days d $hours h left"
            hours > 0 -> "$hours h $minutes m left"
            minutes > 0 -> "$minutes mins left"
            else -> "< 1 min left"
        }
    }

    fun submitCheckIn(
        moodRating: Int,
        urgeIntensity: Int,
        note: String,
        gratitude: String
    ) {
        viewModelScope.launch {
            val checkIn = DailyCheckIn(
                dateKey = todayKey,
                timestamp = System.currentTimeMillis(),
                moodRating = moodRating,
                urgeIntensity = urgeIntensity,
                note = note,
                gratitude = gratitude
            )
            repository.recordCheckIn(checkIn)
        }
    }

    fun pledgeToday() {
        _uiState.value = _uiState.value.copy(isPledgedToday = true)
    }

    fun recordRelapse(
        triggers: List<String>,
        note: String,
        lesson: String
    ) {
        viewModelScope.launch {
            val triggerStr = if (triggers.isEmpty()) "Spontaneous Urge" else triggers.joinToString(", ")
            repository.resetStreak(
                triggers = triggerStr,
                note = note,
                lesson = lesson
            )
        }
    }

    fun recordUrgeOvercome(
        trigger: String,
        copingTool: String,
        intensity: Int,
        reflectionNote: String = ""
    ) {
        viewModelScope.launch {
            val urgeLog = UrgeLog(
                timestamp = System.currentTimeMillis(),
                trigger = trigger,
                intensity = intensity,
                copingTool = copingTool,
                wasOvercome = true,
                reflectionNote = reflectionNote
            )
            repository.recordUrgeLog(urgeLog)
        }
    }

    fun updateHabitSettings(
        name: String,
        pledge: String,
        customStartTimeMillis: Long
    ) {
        viewModelScope.launch {
            val current = _uiState.value.habitProfile
            repository.updateHabitProfile(
                current.copy(
                    habitName = name.ifBlank { "Clean Streak" },
                    pledge = pledge.ifBlank { "I choose clarity, resilience, and presence today." },
                    currentStreakStartTime = customStartTimeMillis
                )
            )
        }
    }

    fun setAppLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            val current = _uiState.value.habitProfile
            repository.updateHabitProfile(
                current.copy(
                    isAppLockEnabled = enabled,
                    pinCode = if (enabled) pin else ""
                )
            )
            _uiState.value = _uiState.value.copy(isAppLocked = false)
        }
    }

    fun unlockApp(enteredPin: String): Boolean {
        val currentPin = _uiState.value.habitProfile.pinCode
        return if (enteredPin == currentPin) {
            _uiState.value = _uiState.value.copy(isAppLocked = false)
            true
        } else {
            false
        }
    }

    fun lockAppNow() {
        if (_uiState.value.habitProfile.isAppLockEnabled) {
            _uiState.value = _uiState.value.copy(isAppLocked = true)
        }
    }

    fun setThemeMode(mode: String) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    fun deleteRelapseLog(id: Long) {
        viewModelScope.launch {
            repository.deleteRelapseLog(id)
        }
    }

    class Factory(private val repository: CleanStreakRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CleanStreakViewModel::class.java)) {
                return CleanStreakViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
