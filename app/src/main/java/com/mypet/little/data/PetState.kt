package com.mypet.little.data

/** وضعیت کامل یک پت. هر اسلات پت جداگانه خودش را دارد. */
data class PetState(
    val slot: Int,
    val characterId: String,
    val name: String,
    val stage: Int = 0, // 0 تخم، 1 نوزاد، 2 کودک، 3 نوجوان، 4 بزرگسال
    val fullness: Float = 70f,
    val happiness: Float = 70f,
    val energy: Float = 80f,
    val cleanliness: Float = 80f,
    val health: Float = 100f,
    val coins: Int = 20,
    val xp: Int = 0,
    val care: Float = 70f, // میانگین کیفیت مراقبت (۰ تا ۱۰۰)
    val bodyColor: Int,
    val eyeColor: Int,
    val accessory: Int = 0,
    val sleeping: Boolean = false,
    val lastUpdate: Long
)
