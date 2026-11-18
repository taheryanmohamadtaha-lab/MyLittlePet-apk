package com.mypet.little.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypet.little.characters.CharacterDef
import com.mypet.little.characters.CharacterRegistry
import com.mypet.little.data.Action
import com.mypet.little.data.PetLogic
import com.mypet.little.data.PetState
import com.mypet.little.data.PetStore
import com.mypet.little.data.Reaction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class Select(val slot: Int) : Screen()
    object Settings : Screen()
    object Shop : Screen()
    object Customize : Screen()
    object Themes : Screen()
    object About : Screen()
}

class PetViewModel(app: Application) : AndroidViewModel(app) {
    private val store = PetStore(app)
    private val sound = SoundManager()

    var pets by mutableStateOf<List<PetState?>>(emptyList())
        private set
    var activeSlot by mutableStateOf(0)
        private set
    var unlocked by mutableStateOf<Set<String>>(emptySet())
        private set
    var soundOn by mutableStateOf(true)
        private set
    var appTitle by mutableStateOf("پت کوچولوی من")
        private set
    var themeId by mutableStateOf("candy")
        private set
    var floatingOn by mutableStateOf(false)
        private set
    var screen by mutableStateOf<Screen>(Screen.Home)
        private set
    var reaction by mutableStateOf(Reaction.NONE)
        private set
    var bubble by mutableStateOf<String?>(null)
        private set

    private var reactionJob: Job? = null
    private var bubbleJob: Job? = null

    val active: PetState? get() = pets.getOrNull(activeSlot)
    val theme: AppTheme get() = AppThemes.get(themeId)
    val hasPets: Boolean get() = pets.any { it != null }

    init {
        soundOn = store.soundOn
        sound.enabled = soundOn
        appTitle = store.appTitle
        themeId = store.themeId
        floatingOn = store.floatingOn
        val now = System.currentTimeMillis()
        pets = store.loadPets().map { p -> p?.let { PetLogic.applyDecay(it, now) } }
        activeSlot = store.activeSlot.coerceIn(0, PetStore.SLOTS - 1)
        if (pets.getOrNull(activeSlot) == null) {
            val first = pets.indexOfFirst { it != null }
            if (first >= 0) activeSlot = first
        }
        unlocked = store.unlocked
        refreshAutoUnlock()
        persist()
        if (!hasPets) screen = Screen.Select(0)
    }

    private fun persist() {
        store.savePets(pets)
        store.activeSlot = activeSlot
    }

    private fun updateActive(p: PetState) {
        pets = pets.mapIndexed { i, old -> if (i == p.slot) p else old }
        persist()
    }

    /** با رسیدن یک پت به مرحله لازم، کاراکتر قفل‌شده خودکار باز می‌شود */
    private fun refreshAutoUnlock() {
        val maxStage = pets.filterNotNull().maxOfOrNull { it.stage } ?: 0
        val set = unlocked.toMutableSet()
        for (c in CharacterRegistry.all) {
            if (c.unlockCoins == 0 || (c.unlockStage in 1..maxStage)) set.add(c.id)
        }
        if (set != unlocked) {
            unlocked = set
            store.unlocked = set
        }
    }

    fun isUnlocked(c: CharacterDef): Boolean = c.unlockCoins == 0 || unlocked.contains(c.id)

    fun go(s: Screen) {
        screen = s
    }

    /** هر چند ثانیه صدا زده می‌شود تا نیازها با گذشت زمان کم شوند */
    fun tick() {
        val now = System.currentTimeMillis()
        pets = pets.map { p -> p?.let { PetLogic.applyDecay(it, now) } }
        refreshAutoUnlock()
        persist()
    }

    fun act(a: Action) {
        val p = active ?: return
        val result = PetLogic.perform(PetLogic.applyDecay(p, System.currentTimeMillis()), a)
        updateActive(result.pet)
        refreshAutoUnlock()
        val ch = CharacterRegistry.get(result.pet.characterId)
        val msg = result.message
            ?: ch.dialogues[PetLogic.mood(result.pet)]?.randomOrNull()
        showBubble(msg)
        showReaction(result.reaction)
        when (result.reaction) {
            Reaction.EVOLVE -> sound.play(ch.soundHz * 2, 400)
            Reaction.REFUSE -> sound.play(ch.soundHz / 2, 120)
            Reaction.NONE -> {}
            else -> sound.play(ch.soundHz, 140)
        }
    }

    fun tapPet() {
        val p = active ?: return
        if (p.sleeping) {
            showBubble("زززز... 💤")
            return
        }
        act(Action.PET)
        val ch = CharacterRegistry.get(p.characterId)
        if (p.stage > 0 && reaction != Reaction.EVOLVE) showBubble(ch.tapLines.randomOrNull())
    }

    private fun showBubble(text: String?) {
        bubbleJob?.cancel()
        bubble = text
        if (text != null) {
            bubbleJob = viewModelScope.launch {
                delay(3500)
                bubble = null
            }
        }
    }

    private fun showReaction(r: Reaction) {
        reactionJob?.cancel()
        reaction = r
        reactionJob = viewModelScope.launch {
            delay(1400)
            reaction = Reaction.NONE
        }
    }

    fun createPet(slot: Int, characterId: String, name: String) {
        val ch = CharacterRegistry.get(characterId)
        val pet = PetState(
            slot = slot,
            characterId = ch.id,
            name = name.trim().ifEmpty { ch.nameFa },
            bodyColor = 0,
            eyeColor = 0,
            lastUpdate = System.currentTimeMillis()
        )
        pets = pets.mapIndexed { i, old -> if (i == slot) pet else old }
        activeSlot = slot
        screen = Screen.Home
        persist()
        showBubble("سلام! من تخم جدیدتم 🥚 نوازشم کن تا بترکم!")
    }

    fun switchSlot(i: Int) {
        if (pets.getOrNull(i) != null) {
            activeSlot = i
            persist()
            bubble = null
        } else {
            screen = Screen.Select(i)
        }
    }

    fun rename(name: String) {
        val p = active ?: return
        if (name.isNotBlank()) updateActive(p.copy(name = name.trim()))
    }

    fun customize(body: Int? = null, eye: Int? = null, accessory: Int? = null) {
        val p = active ?: return
        updateActive(
            p.copy(
                bodyColor = body ?: p.bodyColor,
                eyeColor = eye ?: p.eyeColor,
                accessory = accessory ?: p.accessory
            )
        )
    }

    fun deleteActive() {
        val p = active ?: return
        pets = pets.mapIndexed { i, old -> if (i == p.slot) null else old }
        val next = pets.indexOfFirst { it != null }
        if (next >= 0) {
            activeSlot = next
            screen = Screen.Home
        } else {
            screen = Screen.Select(0)
        }
        persist()
    }

    /** بازکردن کاراکتر قفل‌شده با سکه‌های پت فعال */
    fun unlockWithCoins(c: CharacterDef): Boolean {
        val p = active ?: return false
        if (isUnlocked(c) || p.coins < c.unlockCoins) return false
        updateActive(p.copy(coins = p.coins - c.unlockCoins))
        val set = unlocked + c.id
        unlocked = set
        store.unlocked = set
        return true
    }

    fun firstEmptySlot(): Int = pets.indexOfFirst { it == null }

    fun setTheme(id: String) {
        themeId = id
        store.themeId = id
    }

    fun setFloating(on: Boolean) {
        floatingOn = on
        store.floatingOn = on
    }

    fun setSound(on: Boolean) {
        soundOn = on
        sound.enabled = on
        store.soundOn = on
    }

    fun setTitle(t: String) {
        if (t.isNotBlank()) {
            appTitle = t.trim()
            store.appTitle = appTitle
        }
    }
}
