package com.mypet.little.data

import com.mypet.little.characters.Mood

enum class Action { FEED, PLAY, BATH, SLEEP, MEDICINE, PET }

enum class Reaction { NONE, EAT, PLAY, BATH, SLEEP, WAKE, MEDICINE, PET, EVOLVE, REFUSE }

data class ActionResult(val pet: PetState, val reaction: Reaction, val message: String?)

object PetLogic {
    val stageNames = listOf("تخم", "نوزاد", "کودک", "نوجوان", "بزرگسال")

    /** مقدار XP لازم برای رسیدن به هر مرحله */
    val stageXp = intArrayOf(0, 20, 120, 400, 900)

    private fun c(v: Float): Float = v.coerceIn(0f, 100f)

    fun average(p: PetState): Float =
        (p.fullness + p.happiness + p.energy + p.cleanliness + p.health) / 5f

    /** سطح مراقبت: ۰ بی‌توجهی، ۱ متوسط، ۲ عالی */
    fun tier(p: PetState): Int = when {
        p.care >= 70f -> 2
        p.care >= 40f -> 1
        else -> 0
    }

    fun mood(p: PetState): Mood = when {
        p.sleeping -> Mood.SLEEPING
        p.health < 30f -> Mood.SICK
        p.fullness < 25f -> Mood.HUNGRY
        p.energy < 25f -> Mood.TIRED
        p.happiness < 25f -> Mood.ANGRY
        else -> Mood.HAPPY
    }

    /** کم شدن تدریجی نیازها با گذشت زمان (حتی وقتی برنامه بسته بوده) */
    fun applyDecay(p: PetState, now: Long): PetState {
        val minutes = ((now - p.lastUpdate) / 60000f).coerceIn(0f, 4320f)
        if (minutes <= 0f) return p
        if (p.stage == 0) return p.copy(lastUpdate = now)

        val sl = p.sleeping
        val full = c(p.fullness - minutes * (if (sl) 0.05f else 0.12f))
        val hap = c(p.happiness - minutes * (if (sl) 0.02f else 0.08f))
        val en = c(if (sl) p.energy + minutes * 0.5f else p.energy - minutes * 0.05f)
        val cl = c(p.cleanliness - minutes * 0.06f)
        val low = full < 20f || cl < 20f || en < 10f
        val he = c(
            p.health + when {
                low -> -minutes * 0.08f
                full > 50f && cl > 50f -> minutes * 0.03f
                else -> 0f
            }
        )
        val stillSleeping = sl && en < 100f
        val updated = p.copy(
            fullness = full, happiness = hap, energy = en, cleanliness = cl,
            health = he, sleeping = stillSleeping, lastUpdate = now
        )
        val k = (minutes * 0.003f).coerceAtMost(1f)
        return updated.copy(care = p.care + (average(updated) - p.care) * k)
    }

    private fun gainXp(p: PetState, amount: Int): Pair<PetState, Boolean> {
        val xp = p.xp + amount
        var st = p.stage
        var evolved = false
        while (st < 4 && xp >= stageXp[st + 1]) {
            st++
            evolved = true
        }
        return p.copy(xp = xp, stage = st) to evolved
    }

    private fun finish(p: PetState, xp: Int, r: Reaction): ActionResult {
        val (np, evolved) = gainXp(p, xp)
        val withCare = np.copy(care = np.care * 0.9f + average(np) * 0.1f)
        if (evolved) {
            val msg = if (withCare.stage == 1) "تخم ترکید! ${withCare.name} به دنیا اومد 🐣"
            else "تبریک! ${withCare.name} بزرگ شد: ${stageNames[withCare.stage]} 🎉"
            return ActionResult(withCare, Reaction.EVOLVE, msg)
        }
        return ActionResult(withCare, r, null)
    }

    private fun refuse(p: PetState, msg: String) = ActionResult(p, Reaction.REFUSE, msg)

    fun perform(p: PetState, a: Action): ActionResult {
        if (p.stage == 0) {
            return if (a == Action.PET) {
                finish(p.copy(happiness = c(p.happiness + 5f)), 5, Reaction.PET)
            } else {
                refuse(p, "هنوز تخمه! نوازشش کن تا زودتر بترکه 🥚")
            }
        }
        if (p.sleeping && a != Action.SLEEP) {
            return refuse(p, "${p.name} خوابیده! اول بیدارش کن 😴")
        }
        return when (a) {
            Action.FEED ->
                if (p.fullness >= 95f) refuse(p, "${p.name} سیره! 😋")
                else finish(
                    p.copy(fullness = c(p.fullness + 25f), happiness = c(p.happiness + 2f)),
                    3, Reaction.EAT
                )
            Action.PLAY ->
                if (p.energy < 15f) refuse(p, "${p.name} خیلی خسته‌ست، بذار استراحت کنه 😮‍💨")
                else finish(
                    p.copy(
                        happiness = c(p.happiness + 20f),
                        energy = c(p.energy - 15f),
                        fullness = c(p.fullness - 5f),
                        coins = p.coins + 3
                    ),
                    4, Reaction.PLAY
                )
            Action.BATH ->
                if (p.cleanliness >= 95f) refuse(p, "${p.name} تمیزه! 🛁")
                else finish(p.copy(cleanliness = c(p.cleanliness + 35f)), 2, Reaction.BATH)
            Action.SLEEP ->
                if (p.sleeping) ActionResult(p.copy(sleeping = false), Reaction.WAKE, "صبح بخیر! ☀️")
                else if (p.energy >= 95f) refuse(p, "${p.name} هنوز خوابش نمیاد 😄")
                else ActionResult(p.copy(sleeping = true), Reaction.SLEEP, null)
            Action.MEDICINE ->
                if (p.health >= 95f) refuse(p, "${p.name} سالمه و دوا نمی‌خواد 💪")
                else finish(
                    p.copy(health = c(p.health + 30f), happiness = c(p.happiness - 5f)),
                    1, Reaction.MEDICINE
                )
            Action.PET ->
                finish(p.copy(happiness = c(p.happiness + 8f)), 1, Reaction.PET)
        }
    }
}
