package com.mypet.little.characters

val DragonCharacter = CharacterDef(
    id = "dragon",
    nameFa = "اژدها",
    emoji = "🐲",
    personalityFa = "شجاع، آتشین و دوست‌داشتنی",
    defaultBody = argb(0xFF6BCB77),
    defaultEye = argb(0xFFFFB703),
    bellyColor = argb(0xFFFFF3B0),
    cheekColor = argb(0xFFFF8E72),
    bodyPalette = listOf(
        argb(0xFF6BCB77), argb(0xFF7B9CFF), argb(0xFFFF6B6B),
        argb(0xFFB388EB), argb(0xFFFFB703)
    ),
    earStyle = EarStyle.HORNS,
    tailStyle = TailStyle.DRAGON,
    hasWings = true,
    hasMuzzle = false,
    whiskers = false,
    unlockStage = 2,
    unlockCoins = 150,
    soundHz = 300,
    dialogues = mapOf(
        Mood.HAPPY to listOf("گرررر! من قوی‌ترین اژدهام 🔥", "امروز یه آتیش کوچولو درست کردم!", "تو دوست واقعی منی 🐉"),
        Mood.HUNGRY to listOf("گرر... گوشت می‌خوام 🍖", "آتیشم کم‌رمق شده، غذا بده!"),
        Mood.TIRED to listOf("بال‌هام خسته شدن 😴", "یه خواب توی غار می‌چسبه..."),
        Mood.SICK to listOf("آتیشم خاموش شده... 🤒", "حالم خوب نیست"),
        Mood.ANGRY to listOf("گرررر! دارم دود می‌کنم 😡", "دلم می‌خواد قهر کنم!"),
        Mood.SLEEPING to listOf("گرررر... زززز... 💤")
    ),
    tapLines = listOf("گرر! قلقلکم میاد 🐲", "یه دود کوچولو دادم بیرون!", "بالام تکون خورد!")
)
