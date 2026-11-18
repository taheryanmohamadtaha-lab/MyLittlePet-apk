package com.mypet.little.characters

enum class Mood { HAPPY, HUNGRY, TIRED, SICK, ANGRY, SLEEPING }

enum class EarStyle { POINTY, FLOPPY, LONG, HORNS }

enum class TailStyle { NONE, CAT, DOG, FLUFF, DRAGON }

/** تبدیل کد رنگ هگز به Int (برای نوشتن راحت‌تر رنگ‌ها) */
fun argb(v: Long): Int = v.toInt()

/**
 * مشخصات یک کاراکتر. برای اضافه کردن کاراکتر جدید فقط کافیه
 * یک فایل مثل CatCharacter.kt بسازی و اون رو در CharacterRegistry ثبت کنی.
 */
data class CharacterDef(
    val id: String,
    val nameFa: String,
    val emoji: String,
    val personalityFa: String,
    val defaultBody: Int,
    val defaultEye: Int,
    val bellyColor: Int,
    val cheekColor: Int,
    val bodyPalette: List<Int>,
    val earStyle: EarStyle,
    val tailStyle: TailStyle,
    val hasWings: Boolean,
    val hasMuzzle: Boolean,
    val whiskers: Boolean,
    /** با رسیدن هر پت به این مرحله (۰ تا ۴) کاراکتر خودکار باز می‌شود؛ ۰ یعنی از اول باز است */
    val unlockStage: Int,
    /** یا با پرداخت این تعداد سکه باز می‌شود */
    val unlockCoins: Int,
    /** فرکانس صدای مخصوص کاراکتر */
    val soundHz: Int,
    val dialogues: Map<Mood, List<String>>,
    val tapLines: List<String>
)
