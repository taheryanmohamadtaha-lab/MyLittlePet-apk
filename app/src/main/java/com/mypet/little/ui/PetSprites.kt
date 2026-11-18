package com.mypet.little.ui

import android.content.Context
import com.mypet.little.characters.Mood
import com.mypet.little.data.Reaction
import kotlin.math.cos
import kotlin.math.sin

/**
 * انتخاب تصویر پت از روی کاراکتر شیت (فایل‌های p_<کاراکتر>_<حالت> در پوشه drawable-nodpi)
 * و ساخت فیلتر رنگ (تغییر رنگ بدن، حالت بی‌توجهی و مراقبت عالی).
 */
object PetSprites {
    fun stageKey(stage: Int): String = when (stage) {
        1 -> "baby"
        2, 3 -> "young"
        else -> "adult"
    }

    /** اندازه نسبی پت در هر مرحله رشد */
    fun stageScale(stage: Int): Float = when (stage) {
        1 -> 0.6f
        2 -> 0.72f
        3 -> 0.86f
        else -> 1f
    }

    fun poseFor(mood: Mood, reaction: Reaction): String {
        when (reaction) {
            Reaction.PLAY -> return "play"
            Reaction.EAT, Reaction.PET, Reaction.BATH, Reaction.EVOLVE -> return "happy"
            else -> {}
        }
        return when (mood) {
            Mood.SLEEPING -> "sleep"
            Mood.ANGRY -> "angry"
            else -> "sit"
        }
    }

    /** پوز مناسب برای وقتی که پت "درحال بازی" است (برای پت شناور و انیمیشن) */
    const val POSE_PLAY = "play"

    fun resId(ctx: Context, characterId: String, key: String): Int {
        val pkg = ctx.packageName
        val id = ctx.resources.getIdentifier("p_${characterId}_$key", "drawable", pkg)
        if (id != 0) return id
        return ctx.resources.getIdentifier("p_${characterId}_sit", "drawable", pkg)
    }

    fun moodEmoji(mood: Mood): String? = when (mood) {
        Mood.HUNGRY -> "🍖"
        Mood.TIRED -> "🥱"
        Mood.SICK -> "🤒"
        Mood.ANGRY -> "💢"
        Mood.SLEEPING -> "💤"
        Mood.HAPPY -> null
    }

    // ---------------- فیلتر رنگ ----------------

    private fun identity() = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    /** a را بعد از b اعمال می‌کند */
    private fun mul(a: FloatArray, b: FloatArray): FloatArray {
        val r = FloatArray(20)
        for (i in 0 until 4) {
            for (j in 0 until 5) {
                var s = 0f
                for (k in 0 until 4) s += a[i * 5 + k] * b[k * 5 + j]
                if (j == 4) s += a[i * 5 + 4]
                r[i * 5 + j] = s
            }
        }
        return r
    }

    private fun hueMatrix(deg: Int): FloatArray {
        val rad = Math.toRadians(deg.toDouble())
        val c = cos(rad).toFloat()
        val s = sin(rad).toFloat()
        return floatArrayOf(
            0.213f + c * 0.787f - s * 0.213f, 0.715f - c * 0.715f - s * 0.715f, 0.072f - c * 0.072f + s * 0.928f, 0f, 0f,
            0.213f - c * 0.213f + s * 0.143f, 0.715f + c * 0.285f + s * 0.140f, 0.072f - c * 0.072f - s * 0.283f, 0f, 0f,
            0.213f - c * 0.213f - s * 0.787f, 0.715f - c * 0.715f + s * 0.715f, 0.072f + c * 0.928f + s * 0.072f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    private fun satMatrix(s: Float): FloatArray = floatArrayOf(
        0.213f + 0.787f * s, 0.715f - 0.715f * s, 0.072f - 0.072f * s, 0f, 0f,
        0.213f - 0.213f * s, 0.715f + 0.285f * s, 0.072f - 0.072f * s, 0f, 0f,
        0.213f - 0.213f * s, 0.715f - 0.715f * s, 0.072f + 0.928f * s, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    private fun scaleMatrix(r: Float, g: Float, b: Float): FloatArray = floatArrayOf(
        r, 0f, 0f, 0f, 0f,
        0f, g, 0f, 0f, 0f,
        0f, 0f, b, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    /** hue: چرخش رنگ (۰ تا ۳۶۰)، tier: ۰ بی‌توجهی، ۱ متوسط، ۲ عالی */
    fun matrix(hue: Int, tier: Int, sick: Boolean): FloatArray {
        var m = identity()
        if (hue != 0) m = mul(hueMatrix(hue), m)
        when (tier) {
            0 -> {
                m = mul(satMatrix(0.5f), m)
                m = mul(scaleMatrix(0.88f, 0.88f, 0.9f), m)
            }
            2 -> m = mul(satMatrix(1.12f), m)
        }
        if (sick) m = mul(scaleMatrix(0.82f, 1f, 0.82f), m)
        return m
    }

    /** رنگ بدن ذخیره‌شده قدیمی (ARGB) را به درجه چرخش رنگ تبدیل می‌کند */
    fun hueOf(stored: Int): Int = if (stored in 0..360) stored else 0
}
