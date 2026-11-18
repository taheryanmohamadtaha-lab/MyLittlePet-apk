package com.mypet.little.characters

val CatCharacter = CharacterDef(
    id = "cat",
    nameFa = "گربه",
    emoji = "🐱",
    personalityFa = "کنجکاو، بازیگوش و کمی ناز‌نازی",
    defaultBody = argb(0xFFF4A261),
    defaultEye = argb(0xFF2A9D8F),
    bellyColor = argb(0xFFFFF1DC),
    cheekColor = argb(0xFFFF9AA2),
    bodyPalette = listOf(
        argb(0xFFF4A261), argb(0xFF9AA5B1), argb(0xFF4A4A4A),
        argb(0xFFFFFFFF), argb(0xFFE9C46A)
    ),
    earStyle = EarStyle.POINTY,
    tailStyle = TailStyle.CAT,
    hasWings = false,
    hasMuzzle = false,
    whiskers = true,
    unlockStage = 0,
    unlockCoins = 0,
    soundHz = 880,
    dialogues = mapOf(
        Mood.HAPPY to listOf("میااااو! دوستت دارم 😻", "امروز حس شکارچی بودن دارم!", "پرپر... پرپر... 💕"),
        Mood.HUNGRY to listOf("میو... شکمم قارقار می‌کنه 🐟", "یه ماهی کوچولو بدی بد نیست!"),
        Mood.TIRED to listOf("میو... خوابم میاد 😴", "یه جای گرم می‌خوام..."),
        Mood.SICK to listOf("میو... حالم خوب نیست 🤒", "یه دوا بده لطفاً..."),
        Mood.ANGRY to listOf("هیس! دلم گرفته 😾", "منو تنها گذاشتی!"),
        Mood.SLEEPING to listOf("زززز... 💤")
    ),
    tapLines = listOf("میو! قلقلکم میاد 😸", "بیشتر نازم کن!", "پرپرپر 💗")
)
