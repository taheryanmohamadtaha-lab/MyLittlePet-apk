package com.mypet.little.characters

/**
 * فهرست همه کاراکترها. برای اضافه کردن کاراکتر بعدی (پاندا، روباه، ...)
 * فقط فایل جدید بساز و اسمش رو اینجا به لیست اضافه کن.
 */
object CharacterRegistry {
    val all: List<CharacterDef> = listOf(
        CatCharacter,
        DogCharacter,
        RabbitCharacter,
        DragonCharacter
    )

    fun get(id: String): CharacterDef = all.firstOrNull { it.id == id } ?: all[0]
}
