package com.mypet.little.characters

val DogCharacter = CharacterDef(
    id = "dog",
    nameFa = "سگ",
    emoji = "🐶",
    personalityFa = "وفادار، پرانرژی و همیشه شاد",
    defaultBody = argb(0xFFC68B59),
    defaultEye = argb(0xFF5D4037),
    bellyColor = argb(0xFFFFEBD0),
    cheekColor = argb(0xFFFF8FA3),
    bodyPalette = listOf(
        argb(0xFFC68B59), argb(0xFFFFFFFF), argb(0xFF3E3E3E),
        argb(0xFFE9C46A), argb(0xFFB5838D)
    ),
    earStyle = EarStyle.FLOPPY,
    tailStyle = TailStyle.DOG,
    hasWings = false,
    hasMuzzle = true,
    whiskers = false,
    unlockStage = 0,
    unlockCoins = 0,
    soundHz = 520,
    dialogues = mapOf(
        Mood.HAPPY to listOf("هاپ هاپ! بریم بازی؟ 🐾", "دمم داره می‌چرخه از خوشحالی!", "تو بهترین دوستمی! 🦴"),
        Mood.HUNGRY to listOf("هاپ... استخوان کجاست؟ 🍖", "شکمم خالیه، کمکم کن!"),
        Mood.TIRED to listOf("هاااا... خسته شدم 😮‍💨", "یه چرت کوچولو بزنم؟"),
        Mood.SICK to listOf("کوووو... حالم بده 🤒", "دلم درد می‌کنه..."),
        Mood.ANGRY to listOf("غرررر! کسی باهام بازی نمی‌کنه 😠", "قهر کردم!"),
        Mood.SLEEPING to listOf("خرررر... 💤")
    ),
    tapLines = listOf("هاپ! دوباره نازم کن 🐶", "وای چه حس خوبی!", "دمم تکون می‌خوره!")
)
