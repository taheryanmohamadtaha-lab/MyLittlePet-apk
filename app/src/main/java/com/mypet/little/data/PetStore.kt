package com.mypet.little.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** ذخیره خودکار همه پت‌ها روی گوشی (بدون نیاز به اینترنت) */
class PetStore(context: Context) {
    private val prefs = context.getSharedPreferences("mypet_store", Context.MODE_PRIVATE)

    fun loadPets(): List<PetState?> {
        val result = MutableList<PetState?>(SLOTS) { null }
        try {
            val arr = JSONArray(prefs.getString("pets", "[]") ?: "[]")
            for (i in 0 until arr.length()) {
                val p = fromJson(arr.getJSONObject(i))
                if (p.slot in 0 until SLOTS) result[p.slot] = p
            }
        } catch (e: Exception) {
            // اگر داده خراب بود، با لیست خالی شروع می‌کنیم
        }
        return result
    }

    fun savePets(list: List<PetState?>) {
        val arr = JSONArray()
        list.filterNotNull().forEach { arr.put(toJson(it)) }
        prefs.edit().putString("pets", arr.toString()).apply()
    }

    var activeSlot: Int
        get() = prefs.getInt("active", 0)
        set(v) {
            prefs.edit().putInt("active", v).apply()
        }

    var soundOn: Boolean
        get() = prefs.getBoolean("sound", true)
        set(v) {
            prefs.edit().putBoolean("sound", v).apply()
        }

    var appTitle: String
        get() = prefs.getString("title", "پت کوچولوی من") ?: "پت کوچولوی من"
        set(v) {
            prefs.edit().putString("title", v).apply()
        }

    var unlocked: Set<String>
        get() = prefs.getStringSet("unlocked", emptySet())?.toSet() ?: emptySet()
        set(v) {
            prefs.edit().putStringSet("unlocked", HashSet(v)).apply()
        }

    private fun toJson(p: PetState): JSONObject = JSONObject().apply {
        put("slot", p.slot)
        put("characterId", p.characterId)
        put("name", p.name)
        put("stage", p.stage)
        put("fullness", p.fullness.toDouble())
        put("happiness", p.happiness.toDouble())
        put("energy", p.energy.toDouble())
        put("cleanliness", p.cleanliness.toDouble())
        put("health", p.health.toDouble())
        put("coins", p.coins)
        put("xp", p.xp)
        put("care", p.care.toDouble())
        put("bodyColor", p.bodyColor)
        put("eyeColor", p.eyeColor)
        put("accessory", p.accessory)
        put("sleeping", p.sleeping)
        put("lastUpdate", p.lastUpdate)
    }

    private fun fromJson(o: JSONObject): PetState = PetState(
        slot = o.getInt("slot"),
        characterId = o.getString("characterId"),
        name = o.optString("name", "پت"),
        stage = o.optInt("stage", 0),
        fullness = o.optDouble("fullness", 70.0).toFloat(),
        happiness = o.optDouble("happiness", 70.0).toFloat(),
        energy = o.optDouble("energy", 80.0).toFloat(),
        cleanliness = o.optDouble("cleanliness", 80.0).toFloat(),
        health = o.optDouble("health", 100.0).toFloat(),
        coins = o.optInt("coins", 0),
        xp = o.optInt("xp", 0),
        care = o.optDouble("care", 70.0).toFloat(),
        bodyColor = o.getInt("bodyColor"),
        eyeColor = o.getInt("eyeColor"),
        accessory = o.optInt("accessory", 0),
        sleeping = o.optBoolean("sleeping", false),
        lastUpdate = o.optLong("lastUpdate", System.currentTimeMillis())
    )

    companion object {
        const val SLOTS = 3
    }
}
