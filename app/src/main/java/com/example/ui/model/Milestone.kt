package com.example.ui.model

import java.util.concurrent.TimeUnit

data class Milestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val requiredDurationMillis: Long,
    val iconName: String,
    val levelNumber: Int
)

object Milestones {
    val ALL = listOf(
        Milestone(
            id = "24_hours",
            title = "First Step",
            subtitle = "Survive the critical first 24 hours",
            requiredDurationMillis = TimeUnit.HOURS.toMillis(24),
            iconName = "Sparkles",
            levelNumber = 1
        ),
        Milestone(
            id = "3_days",
            title = "Momentum",
            subtitle = "The chemical fog starts to clear",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(3),
            iconName = "Bolt",
            levelNumber = 2
        ),
        Milestone(
            id = "1_week",
            title = "Seven Suns",
            subtitle = "1 full week of mindful discipline",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(7),
            iconName = "WbSunny",
            levelNumber = 3
        ),
        Milestone(
            id = "2_weeks",
            title = "Fortitude",
            subtitle = "Habit pathways begin rewriting",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(14),
            iconName = "Shield",
            levelNumber = 4
        ),
        Milestone(
            id = "1_month",
            title = "New Moon",
            subtitle = "30 clean days of conscious awareness",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(30),
            iconName = "Nightlight",
            levelNumber = 5
        ),
        Milestone(
            id = "2_months",
            title = "Deep Roots",
            subtitle = "Resilience is becoming your default state",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(60),
            iconName = "Spa",
            levelNumber = 6
        ),
        Milestone(
            id = "90_days",
            title = "Dopamine Reset",
            subtitle = "The neurochemical golden standard",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(90),
            iconName = "Psychology",
            levelNumber = 7
        ),
        Milestone(
            id = "6_months",
            title = "Iron Will",
            subtitle = "Half a year of unbroken mastery",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(180),
            iconName = "WorkspacePremium",
            levelNumber = 8
        ),
        Milestone(
            id = "1_year",
            title = "Transcendence",
            subtitle = "A full revolution around the sun",
            requiredDurationMillis = TimeUnit.DAYS.toMillis(365),
            iconName = "Diamond",
            levelNumber = 9
        )
    )
}

data class MilestoneProgress(
    val milestone: Milestone,
    val isUnlocked: Boolean,
    val progress: Float, // 0f to 1f
    val remainingFormatted: String
)
