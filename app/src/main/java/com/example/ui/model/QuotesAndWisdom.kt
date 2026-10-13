package com.example.ui.model

data class Quote(
    val text: String,
    val author: String
)

data class CopingTip(
    val title: String,
    val category: String, // "Physical", "Sensory", "Mental"
    val description: String,
    val iconKey: String
)

object QuotesAndWisdom {
    val DAILY_QUOTES = listOf(
        Quote("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear"),
        Quote("Between stimulus and response there is a space. In that space is our power to choose our response.", "Viktor Frankl"),
        Quote("The urge is merely a visitor passing through your body. You don't have to invite it to stay for dinner.", "Mindful Recovery"),
        Quote("Self-control is strength. Right thought is mastery. Calmness is power.", "James Allen"),
        Quote("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        Quote("A river cuts through rock, not because of its power, but because of its persistence.", "Jim Watkins"),
        Quote("Peace comes from within. Do not seek it without.", "Buddha"),
        Quote("He who conquers himself is the mightiest warrior.", "Confucius"),
        Quote("Every time you resist an urge, your neural pathways physically rewire towards freedom.", "Neuroscience of Habit")
    )

    val SOS_MANTRAS = listOf(
        "This urge is an uncomfortable sensation, not an absolute command.",
        "Cravings always crest like ocean waves and subside within 15 minutes.",
        "Giving in will not solve the underlying craving; it only resets the battle.",
        "I am safe right now in this present moment. I am in full control.",
        "Temporary discomfort now brings lifelong self-respect tomorrow.",
        "Breathe through the tension. Watch it evaporate like morning mist."
    )

    val COPING_TIPS = listOf(
        CopingTip(
            title = "Cold Water Reset",
            category = "Physical",
            description = "Splash freezing cold water on your face or immerse hands in cold water. This activates the mammalian dive reflex, immediately dropping your heart rate and soothing the nervous system.",
            iconKey = "WaterDrop"
        ),
        CopingTip(
            title = "High-Intensity Burst",
            category = "Physical",
            description = "Do 25 pushups, 30 jumping jacks, or a fast 3-minute brisk walk. Burns off excess adrenaline and cortisol rapidly.",
            iconKey = "FitnessCenter"
        ),
        CopingTip(
            title = "5-4-3-2-1 Sensory Grounding",
            category = "Sensory",
            description = "Look around you: Name 5 things you see, 4 you can touch, 3 you hear, 2 you smell, and 1 you taste. Pulls your brain out of urge autopilot into present reality.",
            iconKey = "Visibility"
        ),
        CopingTip(
            title = "Ice Glass Hydration",
            category = "Sensory",
            description = "Drink a large glass of ice water slowly through a straw. Focus on the cold sensation travelling through your throat and stomach.",
            iconKey = "LocalDrink"
        ),
        CopingTip(
            title = "Urge Surfing (10 Mins)",
            category = "Mental",
            description = "Observe the physical urge like an ocean wave without fighting it. Notice its shape, temperature, and intensity, watching it peak and dissolve.",
            iconKey = "Waves"
        ),
        CopingTip(
            title = "Reach Out / Vent",
            category = "Mental",
            description = "Send a text or call a trusted friend or family member, or write down raw uncensored thoughts in a journal to diffuse the mental loop.",
            iconKey = "Chat"
        )
    )

    val COMMON_TRIGGERS = listOf(
        "Stress & Pressure",
        "Boredom & Idleness",
        "Late Night / Fatigue",
        "Loneliness & Isolation",
        "Social Media & Scrolling",
        "Anxiety & Overwhelm",
        "Anger & Frustration",
        "Celebration / Overconfidence",
        "Spontaneous Urge"
    )
}
