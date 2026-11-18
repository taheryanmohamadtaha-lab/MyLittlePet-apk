package com.mypet.little.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypet.little.characters.CharacterDef
import com.mypet.little.characters.CharacterRegistry
import com.mypet.little.characters.Mood
import com.mypet.little.characters.argb
import com.mypet.little.data.Action
import com.mypet.little.data.PetLogic
import com.mypet.little.data.PetStore

private val BgBrush = Brush.verticalGradient(listOf(Color(0xFFFFF0F7), Color(0xFFE8F4FF)))
private val Pink = Color(0xFFFF6FA5)

// ------------------------------------------------------------------ اجزای مشترک

@Composable
private fun Header(title: String, onBack: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            Text(
                "➡️", fontSize = 24.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBack() }
                    .padding(8.dp)
            )
        }
        Text(
            title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Pink,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun Pill(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Pink else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text, fontSize = 14.sp,
            color = if (selected) Color.White else Color(0xFF444444),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StatBar(emoji: String, label: String, value: Float, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 18.sp, modifier = Modifier.width(30.dp))
        Text(label, fontSize = 13.sp, modifier = Modifier.width(52.dp))
        LinearProgressIndicator(
            progress = { (value / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = Color(0x22000000)
        )
        Text(
            "${value.toInt()}", fontSize = 12.sp, textAlign = TextAlign.Center,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
private fun ActionButton(emoji: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 26.sp)
        Text(label, fontSize = 12.sp)
    }
}

// ------------------------------------------------------------------ صفحه اصلی

@Composable
fun HomeScreen(vm: PetViewModel) {
    val pet = vm.active ?: return
    val ch = CharacterRegistry.get(pet.characterId)
    val mood = PetLogic.mood(pet)
    val tier = PetLogic.tier(pet)

    Column(
        Modifier
            .fillMaxSize()
            .background(BgBrush)
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                vm.appTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Pink,
                modifier = Modifier.weight(1f)
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF3C4))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("🪙 ${pet.coins}", fontWeight = FontWeight.Bold) }
            Text(
                "⚙️", fontSize = 26.sp,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clip(CircleShape)
                    .clickable { vm.go(Screen.Settings) }
                    .padding(6.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until PetStore.SLOTS) {
                val p = vm.pets.getOrNull(i)
                val label = if (p == null) "➕ جدید" else CharacterRegistry.get(p.characterId).emoji + " " + p.name
                Pill(label, i == vm.activeSlot) { vm.switchSlot(i) }
            }
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PetView(
                character = ch, stage = pet.stage, tier = tier, mood = mood,
                bodyColor = pet.bodyColor, eyeColor = pet.eyeColor, accessory = pet.accessory,
                reaction = vm.reaction,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { vm.tapPet() }
            )
            val text = vm.bubble
            if (text != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text, fontSize = 14.sp, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                }
            }
            if (pet.sleeping) {
                Text("💤", fontSize = 36.sp, modifier = Modifier.align(Alignment.TopEnd))
            }
        }

        val tierText = when (tier) {
            2 -> "مراقبت عالی ✨"
            1 -> "مراقبت متوسط"
            else -> "نیاز به توجه بیشتر 😢"
        }
        Text(
            "${pet.name} • ${PetLogic.stageNames[pet.stage]} • $tierText",
            fontSize = 14.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
        )
        if (pet.stage < 4) {
            val from = PetLogic.stageXp[pet.stage]
            val to = PetLogic.stageXp[pet.stage + 1]
            StatBar("🌱", "رشد", (pet.xp - from).toFloat() / (to - from).toFloat() * 100f, Color(0xFF66BB6A))
        }
        if (pet.stage > 0) {
            StatBar("🍖", "سیری", pet.fullness, Color(0xFFFF9800))
            StatBar("😊", "شادی", pet.happiness, Color(0xFFFFC107))
            StatBar("⚡", "انرژی", pet.energy, Color(0xFF42A5F5))
            StatBar("🫧", "تمیزی", pet.cleanliness, Color(0xFF26C6DA))
            StatBar("❤️", "سلامتی", pet.health, Color(0xFFEF5350))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton("🍖", "غذا", Modifier.weight(1f)) { vm.act(Action.FEED) }
            ActionButton("🎾", "بازی", Modifier.weight(1f)) { vm.act(Action.PLAY) }
            ActionButton("🛁", "حمام", Modifier.weight(1f)) { vm.act(Action.BATH) }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                if (pet.sleeping) "☀️" else "🛏️",
                if (pet.sleeping) "بیدار کن" else "بخواب",
                Modifier.weight(1f)
            ) { vm.act(Action.SLEEP) }
            ActionButton("💊", "دارو", Modifier.weight(1f)) { vm.act(Action.MEDICINE) }
            ActionButton("🤗", "نوازش", Modifier.weight(1f)) { vm.act(Action.PET) }
        }
    }
}

// ------------------------------------------------------------------ انتخاب کاراکتر

@Composable
private fun CharacterCard(ch: CharacterDef, unlocked: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(10.dp)
            .alpha(if (unlocked) 1f else 0.55f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PetView(
            character = ch, stage = 3, tier = 1, mood = Mood.HAPPY,
            bodyColor = ch.defaultBody, eyeColor = ch.defaultEye, accessory = 0,
            modifier = Modifier.size(120.dp)
        )
        Text("${ch.emoji} ${ch.nameFa}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(ch.personalityFa, fontSize = 11.sp, textAlign = TextAlign.Center)
        if (!unlocked) {
            Text(
                "🔒 باز شدن: رسیدن یک پت به مرحله «${PetLogic.stageNames[ch.unlockStage]}» یا ${ch.unlockCoins} سکه",
                fontSize = 10.sp, textAlign = TextAlign.Center, color = Color(0xFFB71C1C)
            )
        }
    }
}

@Composable
fun SelectScreen(vm: PetViewModel, slot: Int) {
    var chosen by remember { mutableStateOf<CharacterDef?>(null) }
    var name by remember { mutableStateOf("") }
    val occupied = vm.pets.getOrNull(slot)

    Column(
        Modifier
            .fillMaxSize()
            .background(BgBrush)
            .padding(12.dp)
    ) {
        Header("کاراکترت رو انتخاب کن", if (vm.hasPets) ({ vm.go(Screen.Home) }) else null)
        if (occupied != null) {
            Text(
                "⚠️ این اسلات الان «${occupied.name}» رو داره و با انتخاب جدید جایگزین می‌شه.",
                fontSize = 12.sp, color = Color(0xFFB71C1C), modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(CharacterRegistry.all) { ch ->
                val ok = vm.isUnlocked(ch)
                CharacterCard(ch, ok) {
                    if (ok) {
                        name = ch.nameFa
                        chosen = ch
                    }
                }
            }
        }
    }

    val ch = chosen
    if (ch != null) {
        AlertDialog(
            onDismissRequest = { chosen = null },
            title = { Text("اسم پتت چیه؟") },
            text = {
                Column {
                    if (occupied != null) {
                        Text(
                            "توجه: «${occupied.name}» پاک می‌شه و پیشرفتش از بین می‌ره!",
                            color = Color(0xFFB71C1C), fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 14) name = it },
                        singleLine = true,
                        label = { Text("نام پت") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.createPet(slot, ch.id, name)
                    chosen = null
                }) { Text(if (occupied != null) "تأیید و جایگزین کن" else "ثبت") }
            },
            dismissButton = { TextButton(onClick = { chosen = null }) { Text("انصراف") } }
        )
    }
}

// ------------------------------------------------------------------ تنظیمات

@Composable
fun SettingsScreen(vm: PetViewModel) {
    val pet = vm.active ?: return
    var newName by remember(pet.slot) { mutableStateOf(pet.name) }
    var title by remember { mutableStateOf(vm.appTitle) }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgBrush)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("تنظیمات", { vm.go(Screen.Home) })

        Text("اسم پت فعال", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = newName,
            onValueChange = { if (it.length <= 14) newName = it },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { vm.rename(newName) }, modifier = Modifier.fillMaxWidth()) { Text("ذخیره اسم پت") }

        Text("اسم برنامه", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 20) title = it },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { vm.setTitle(title) }, modifier = Modifier.fillMaxWidth()) { Text("ذخیره اسم برنامه") }

        Button(onClick = { vm.go(Screen.Customize) }, modifier = Modifier.fillMaxWidth()) {
            Text("🎨 سفارشی‌سازی ظاهر (رنگ، چشم، اکسسوری)")
        }
        Button(
            onClick = {
                val e = vm.firstEmptySlot()
                vm.go(Screen.Select(if (e >= 0) e else pet.slot))
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("🔄 تغییر کاراکتر / پت جدید") }
        Text(
            "اگه اسلات خالی داشته باشی پت جدید ساخته می‌شه و پت‌های قبلی می‌مونن. اگه هر ۳ اسلات پُر باشن، پت فعلی با پیام تأیید جایگزین می‌شه.",
            fontSize = 12.sp, color = Color(0xFF666666)
        )
        Button(onClick = { vm.go(Screen.Shop) }, modifier = Modifier.fillMaxWidth()) { Text("🛍️ فروشگاه") }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🔊 صدا", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Switch(checked = vm.soundOn, onCheckedChange = { vm.setSound(it) })
        }

        Button(
            onClick = { confirmDelete = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            modifier = Modifier.fillMaxWidth()
        ) { Text("🗑️ حذف این پت") }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("حذف ${pet.name}؟") },
            text = { Text("همه پیشرفت، سکه‌ها و ظاهر این پت برای همیشه پاک می‌شه. مطمئنی؟") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    vm.deleteActive()
                }) { Text("بله، حذف کن", color = Color(0xFFE53935)) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("انصراف") } }
        )
    }
}

// ------------------------------------------------------------------ سفارشی‌سازی

@Composable
private fun SwatchRow(colors: List<Int>, selected: Int, onPick: (Int) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (c in colors) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(c))
                    .border(if (c == selected) 3.dp else 1.dp, if (c == selected) Color(0xFF333333) else Color(0x33000000), CircleShape)
                    .clickable { onPick(c) }
            )
        }
    }
}

@Composable
fun CustomizeScreen(vm: PetViewModel) {
    val pet = vm.active ?: return
    val ch = CharacterRegistry.get(pet.characterId)
    val eyeColors = listOf(
        argb(0xFF2A9D8F), argb(0xFF5D4037), argb(0xFFE5446D),
        argb(0xFFFFB703), argb(0xFF3A86FF), argb(0xFF8338EC)
    )
    val accessories = listOf("هیچی", "🎀 پاپیون", "🎉 کلاه", "🕶️ عینک")

    Column(
        Modifier
            .fillMaxSize()
            .background(BgBrush)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("سفارشی‌سازی ${pet.name}", { vm.go(Screen.Settings) })
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PetView(
                character = ch, stage = maxOf(pet.stage, 2), tier = PetLogic.tier(pet),
                mood = Mood.HAPPY, bodyColor = pet.bodyColor, eyeColor = pet.eyeColor,
                accessory = pet.accessory, modifier = Modifier.size(220.dp)
            )
        }
        Text("رنگ بدن", fontWeight = FontWeight.Bold)
        SwatchRow(ch.bodyPalette, pet.bodyColor) { vm.customize(body = it) }
        Text("رنگ چشم", fontWeight = FontWeight.Bold)
        SwatchRow(eyeColors, pet.eyeColor) { vm.customize(eye = it) }
        Text("اکسسوری", fontWeight = FontWeight.Bold)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accessories.forEachIndexed { i, label ->
                Pill(label, pet.accessory == i) { vm.customize(accessory = i) }
            }
        }
    }
}

// ------------------------------------------------------------------ فروشگاه

@Composable
fun ShopScreen(vm: PetViewModel) {
    val pet = vm.active ?: return
    Column(
        Modifier
            .fillMaxSize()
            .background(BgBrush)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("🛍️ فروشگاه", { vm.go(Screen.Settings) })
        Text("سکه‌های ${pet.name}: 🪙 ${pet.coins}", fontWeight = FontWeight.Bold)
        Text("کاراکترها", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        for (ch in CharacterRegistry.all) {
            val ok = vm.isUnlocked(ch)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PetView(
                    character = ch, stage = 3, tier = 1, mood = Mood.HAPPY,
                    bodyColor = ch.defaultBody, eyeColor = ch.defaultEye, accessory = 0,
                    modifier = Modifier.size(72.dp)
                )
                Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text("${ch.emoji} ${ch.nameFa}", fontWeight = FontWeight.Bold)
                    if (ok) {
                        Text("✅ باز شده", fontSize = 12.sp, color = Color(0xFF2E7D32))
                    } else {
                        Text(
                            "با رسیدن یک پت به «${PetLogic.stageNames[ch.unlockStage]}» خودکار باز می‌شه",
                            fontSize = 11.sp
                        )
                    }
                }
                if (!ok) {
                    Button(
                        onClick = { vm.unlockWithCoins(ch) },
                        enabled = pet.coins >= ch.unlockCoins
                    ) { Text("${ch.unlockCoins} 🪙") }
                }
            }
        }
        Text(
            "🛒 غذا، اسباب‌بازی، لباس، کلاه، پس‌زمینه و دکور خونه در مرحله بعدی به فروشگاه اضافه می‌شن.",
            fontSize = 12.sp, color = Color(0xFF666666)
        )
        Button(
            onClick = {
                val e = vm.firstEmptySlot()
                vm.go(Screen.Select(if (e >= 0) e else pet.slot))
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("🔄 تغییر کاراکتر / پت جدید") }
    }
}
