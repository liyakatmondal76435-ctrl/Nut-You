package com.example.ui.model

enum class BreathPhase(val label: String, val instruction: String) {
    INHALE("Inhale", "Fill your lungs slowly and deeply through your nose"),
    HOLD_IN("Hold", "Gently retain your breath, relaxing your shoulders"),
    EXHALE("Exhale", "Release completely and steadily through your mouth"),
    HOLD_OUT("Rest", "Stay empty and still in calm anticipation")
}

data class BreathStep(
    val phase: BreathPhase,
    val durationSeconds: Int
)

enum class BreathingPattern(
    val title: String,
    val description: String,
    val steps: List<BreathStep>
) {
    BOX_BREATHING(
        title = "Box Breathing (4-4-4-4)",
        description = "Navy SEAL technique to instantly regulate heart rate and nervous system.",
        steps = listOf(
            BreathStep(BreathPhase.INHALE, 4),
            BreathStep(BreathPhase.HOLD_IN, 4),
            BreathStep(BreathPhase.EXHALE, 4),
            BreathStep(BreathPhase.HOLD_OUT, 4)
        )
    ),
    RELAXING_478(
        title = "4-7-8 Deep Calm",
        description = "Dr. Weil's natural tranquilizer for the nervous system to stop urge panic.",
        steps = listOf(
            BreathStep(BreathPhase.INHALE, 4),
            BreathStep(BreathPhase.HOLD_IN, 7),
            BreathStep(BreathPhase.EXHALE, 8)
        )
    ),
    CALM_FLOW(
        title = "Calm Pacing (4-6)",
        description = "Extended exhalation to trigger the parasympathetic brake.",
        steps = listOf(
            BreathStep(BreathPhase.INHALE, 4),
            BreathStep(BreathPhase.EXHALE, 6)
        )
    )
}
