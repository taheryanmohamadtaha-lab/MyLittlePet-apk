package com.mypet.little.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypet.little.characters.CharacterDef
import com.mypet.little.characters.CharacterRegistry
import com.mypet.little.characters.Mood
import com.mypet.little.R
import com.mypet.little.data.Action
import com.mypet.little.data.PetLogic
import com.mypet.little.data.PetStore


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
            title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accentColor(),
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
            .background(if (selected) accentColor() else Color.White)
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
            .background(themeBrush())
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.creator_photo),
                contentDescription = "سازنده",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, accentColor(), CircleShape)
                    .clickable { vm.go(Screen.About) }
            )
            Text(
                vm.appTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accentColor(),
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
            .background(themeBrush())
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
    var needPermission by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(themeBrush())
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("تنظیمات", { vm.go(Screen.Home) })

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "🪟 پت شناور روی صفحه گوشی", modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
            Switch(checked = vm.floatingOn, onCheckedChange = { want ->
                if (want) {
                    if (FloatingPet.canDraw(ctx)) {
                        FloatingPet.start(ctx)
                        vm.setFloating(true)
                        needPermission = false
                    } else {
                        FloatingPet.requestPermission(ctx)
                        needPermission = true
                    }
                } else {
                    FloatingPet.stop(ctx)
                    vm.setFloating(false)
                }
            })
        }
        Text(
            if (needPermission) "در صفحه‌ای که باز شد، اجازه «نمایش روی سایر برنامه‌ها» را روشن کن، برگرد و دوباره این کلید را بزن."
            else "پت کوچولو متحرک روی همه برنامه‌ها و صفحه اصلی می‌آید. با انگشت جابه‌جایش کن، یک ضربه = حرف زدن، لمس طولانی = باز شدن برنامه.",
            fontSize = 12.sp, color = Color(0xFF666666)
        )
        Button(onClick = { vm.go(Screen.Themes) }, modifier = Modifier.fillMaxWidth()) {
            Text("🎨 تم برنامه (${vm.theme.emoji} ${vm.theme.nameFa})")
        }

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
            Text("🌈 تغییر رنگ پت")
        }
        Button(onClick = { vm.go(Screen.About) }, modifier = Modifier.fillMaxWidth()) {
            Text("👦 درباره سازنده")
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

// ------------------------------------------------------------------ سفارشی‌سازی و تم و سازنده

@Composable
fun CustomizeScreen(vm: PetViewModel) {
    val pet = vm.active ?: return
    val ch = CharacterRegistry.get(pet.characterId)
    val hues = listOf(0, 45, 90, 150, 200, 260, 310)
    val current = PetSprites.hueOf(pet.bodyColor)

    Column(
        Modifier
            .fillMaxSize()
            .background(themeBrush())
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("رنگ ${pet.name}", { vm.go(Screen.Settings) })
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PetView(
                character = ch, stage = maxOf(pet.stage, 2), tier = PetLogic.tier(pet),
                mood = Mood.HAPPY, bodyColor = current, eyeColor = 0,
                accessory = 0, modifier = Modifier.size(240.dp)
            )
        }
        Text("یکی از رنگ‌ها را انتخاب کن:", fontWeight = FontWeight.Bold)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (h in hues) {
                Box(
                    Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(
                            if (h == current) 3.dp else 1.dp,
                            if (h == current) accentColor() else Color(0x22000000),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { vm.customize(body = h) },
                    contentAlignment = Alignment.Center
                ) {
                    PetView(
                        character = ch, stage = 2, tier = 1, mood = Mood.HAPPY,
                        bodyColor = h, eyeColor = 0, accessory = 0, modifier = Modifier.size(66.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemesScreen(vm: PetViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .background(themeBrush())
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header("🎨 تم برنامه", { vm.go(Screen.Settings) })
        Text("تم دلخواهت را انتخاب کن؛ بلافاصله اعمال می‌شود.", fontSize = 13.sp)
        for (t in AppThemes.all) {
            val selected = t.id == vm.themeId
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(t.top, t.bottom)))
                    .border(
                        if (selected) 3.dp else 1.dp,
                        if (selected) t.accent else Color(0x22000000),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { vm.setTheme(t.id) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(t.emoji, fontSize = 28.sp)
                Text(
                    t.nameFa, fontWeight = FontWeight.Bold, fontSize = 17.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                )
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(t.accent)
                )
                if (selected) Text("  ✅", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AboutScreen(vm: PetViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .background(themeBrush())
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Header("درباره سازنده", { vm.go(if (vm.hasPets) Screen.Settings else Screen.Home) })
        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(R.drawable.creator_photo),
            contentDescription = "محمدطاها طاهریان",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .border(5.dp, accentColor(), CircleShape)
        )
        Text("محمدطاها طاهریان", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("سازنده برنامه", fontSize = 15.sp, color = accentColor(), fontWeight = FontWeight.Bold)
        Surface(
            shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "«${vm.appTitle}» با عشق برای همه پت‌دوست‌ها ساخته شده است 🐱🐶🐰🐲\nنسخه ۱٫۰",
                modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center, fontSize = 14.sp
            )
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
            .background(themeBrush())
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
