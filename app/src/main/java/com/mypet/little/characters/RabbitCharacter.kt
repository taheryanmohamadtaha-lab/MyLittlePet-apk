package com.mypet.little.characters

val RabbitCharacter = CharacterDef(
    id = "rabbit",
    nameFa = "خرگوش",
    emoji = "🐰",
    personalityFa = "خجالتی، مهربون و هویج‌دوست",
    defaultBody = argb(0xFFF1E3F3),
    defaultEye = argb(0xFFE5446D),
    bellyColor = argb(0xFFFFFFFF),
    cheekColor = argb(0xFFFFA6C1),
    bodyPalette = listOf(
        argb(0xFFF1E3F3), argb(0xFFD7C0AE), argb(0xFF9AA5B1),
        argb(0xFFFFD6E0), argb(0xFFCDEAC0)
    ),
    earStyle = EarStyle.LONG,
    tailStyle = TailStyle.FLUFF,
    hasWings = false,
    hasMuzzle = false,
    whiskers = true,
    unlockStage = 0,
    unlockCoins = 0,
    soundHz = 1040,
    dialogues = mapOf(
        Mood.HAPPY to listOf("بپر بپر! خیلی خوشحالم 🥕", "گوش‌هام از شادی تکون می‌خورن!", "تو خیلی مهربونی 💖"),
        Mood.HUNGRY to listOf("یه هویج کوچولو می‌خوام 🥕", "شکمم گرسنه‌ست..."),
        Mood.TIRED to listOf("چشمام سنگین شده 😴", "بذار یه کم بخوابم..."),
        Mood.SICK to listOf("حالم خوب نیست... 🤒", "یه کم دوا لازم دارم"),
        Mood.ANGRY to listOf("قهرم! 😤", "هیچکس بهم توجه نمی‌کنه!"),
        Mood.SLEEPING to listOf("زززز... 💤")
    ),
    tapLines = listOf("هی! گوش‌هام قلقلکی‌ان 🐰", "آخ جون نوازش!", "قلبم داره تند می‌زنه 💗")
)
