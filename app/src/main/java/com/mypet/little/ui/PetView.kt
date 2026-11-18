package com.mypet.little.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypet.little.characters.CharacterDef
import com.mypet.little.characters.Mood
import com.mypet.little.data.Reaction

/**
 * نمایش پت با تصاویر کاراکتر شیت.
 * stage: مرحله رشد (۰ تخم ... ۴ بزرگسال)
 * tier: کیفیت مراقبت (۰ بی‌توجهی، ۱ متوسط، ۲ عالی)
 * bodyColor: درجه چرخش رنگ بدن (۰ = رنگ اصلی)
 */
@Composable
fun PetView(
    character: CharacterDef,
    stage: Int,
    tier: Int,
    mood: Mood,
    bodyColor: Int,
    eyeColor: Int,
    accessory: Int,
    modifier: Modifier = Modifier,
    reaction: Reaction = Reaction.NONE
) {
    val transition = rememberInfiniteTransition(label = "pet")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Reverse),
        label = "bob"
    )
    val sparkle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "sparkle"
    )
    val amp = when {
        reaction != Reaction.NONE && reaction != Reaction.REFUSE -> 3f
        mood == Mood.SLEEPING -> 0.3f
        else -> 1f
    }

    if (stage == 0) {
        Canvas(modifier) { drawEgg(Color(character.defaultBody), bob, amp) }
        return
    }

    val ctx = LocalContext.current
    val pose = PetSprites.poseFor(mood, reaction)
    val resId = remember(character.id, pose) { PetSprites.resId(ctx, character.id, pose) }
    // آیا پوز مخصوص مرحله رشد (نوزاد/جوان/بزرگسال) باید استفاده شود؟
    // برای پوزهای مخصوص (خواب، بازی، ...) همیشه تصویر بزرگسال است و با اندازه مرحله کوچک می‌شود.
    val baseKey = if (pose == "sit") PetSprites.stageKey(stage) else pose
    val finalRes = remember(character.id, baseKey) { PetSprites.resId(ctx, character.id, baseKey) }
    val id = if (finalRes != 0) finalRes else resId
    val hue = PetSprites.hueOf(bodyColor)
    val filter = remember(hue, tier, mood) {
        ColorFilter.colorMatrix(ColorMatrix(PetSprites.matrix(hue, tier, mood == Mood.SICK)))
    }
    val scale = PetSprites.stageScale(stage)

    Box(modifier) {
        if (tier == 2) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    Color(1f, 0.84f, 0.31f, 0.14f + 0.16f * sparkle),
                    size.minDimension * 0.42f * (0.5f + scale * 0.5f),
                    Offset(size.width / 2f, size.height * 0.58f)
                )
                val a = 0.4f + 0.6f * sparkle
                val sc = Color(1f, 0.9f, 0.4f, a)
                val u = size.minDimension / 100f
                sparkleStar(size.width * 0.14f, size.height * 0.28f, 5f * u * (0.6f + sparkle * 0.6f), sc)
                sparkleStar(size.width * 0.88f, size.height * 0.36f, 4.5f * u * (1.2f - sparkle * 0.6f), sc)
                sparkleStar(size.width * 0.8f, size.height * 0.8f, 4f * u * (1.2f - sparkle * 0.6f), sc)
            }
        }
        if (id != 0) {
            Image(
                painter = painterResource(id),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.BottomCenter,
                colorFilter = filter,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(scale)
                    .fillMaxHeight(scale)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        translationY = -bob * 10f * amp
                        val breathe = 1f + 0.018f * bob * amp.coerceAtMost(1.5f)
                        scaleX = 2f - breathe
                        scaleY = breathe
                    }
            )
        }
        val emoji = PetSprites.moodEmoji(mood)
        if (emoji != null) {
            Text(
                emoji, fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .alpha(0.6f + 0.4f * sparkle)
            )
        }
    }
}

private fun DrawScope.sparkleStar(x: Float, y: Float, r: Float, color: Color) {
    val p = Path()
    p.moveTo(x, y - r)
    p.lineTo(x + r * 0.3f, y - r * 0.3f)
    p.lineTo(x + r, y)
    p.lineTo(x + r * 0.3f, y + r * 0.3f)
    p.lineTo(x, y + r)
    p.lineTo(x - r * 0.3f, y + r * 0.3f)
    p.lineTo(x - r, y)
    p.lineTo(x - r * 0.3f, y - r * 0.3f)
    p.close()
    drawPath(p, color)
}

private fun DrawScope.drawEgg(base: Color, bob: Float, amp: Float) {
    val u = size.minDimension / 100f
    val cx = size.width / 2f
    val ey = 66f * u
    val spot = base.copy(alpha = 0.55f)
    rotate(
        degrees = (bob - 0.5f) * 12f * amp,
        pivot = Offset(cx, 90f * u)
    ) {
        drawOval(Color(0x22000000), Offset(cx - 22f * u, 88f * u), Size(44f * u, 7f * u))
        drawOval(lerp(base, Color.White, 0.55f), Offset(cx - 22f * u, ey - 30f * u), Size(44f * u, 56f * u))
        drawCircle(spot, 6f * u, Offset(cx - 8f * u, ey - 6f * u))
        drawCircle(spot, 4.5f * u, Offset(cx + 9f * u, ey + 8f * u))
        drawCircle(spot, 3.5f * u, Offset(cx + 4f * u, ey - 18f * u))
        drawCircle(spot, 5f * u, Offset(cx - 10f * u, ey + 14f * u))
        drawOval(Color.White.copy(alpha = 0.5f), Offset(cx - 14f * u, ey - 22f * u), Size(8f * u, 14f * u))
    }
}
