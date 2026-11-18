package com.mypet.little.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.mypet.little.characters.CharacterDef
import com.mypet.little.characters.EarStyle
import com.mypet.little.characters.Mood
import com.mypet.little.characters.TailStyle
import com.mypet.little.data.Reaction

/**
 * رسم پت با Canvas (بدون هیچ فایل تصویری).
 * stage: مرحله رشد (۰ تا ۴)، tier: کیفیت مراقبت (۰ بی‌توجهی، ۱ متوسط، ۲ عالی)
 * ترکیب این دو برای هر کاراکتر ۱۲ شکل متفاوت می‌سازه.
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
        mood == Mood.SLEEPING -> 0.4f
        else -> 1f
    }
    Canvas(modifier = modifier) {
        drawPet(character, stage, tier, mood, bodyColor, eyeColor, accessory, bob, sparkle, amp)
    }
}

private val Ink = Color(0xFF3B2F2F)

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

private fun DrawScope.triangle(a: Offset, b: Offset, c: Offset, color: Color) {
    val p = Path()
    p.moveTo(a.x, a.y)
    p.lineTo(b.x, b.y)
    p.lineTo(c.x, c.y)
    p.close()
    drawPath(p, color)
}

private fun DrawScope.drawPet(
    ch: CharacterDef,
    stage: Int,
    tier: Int,
    mood: Mood,
    bodyInt: Int,
    eyeInt: Int,
    accessory: Int,
    bob: Float,
    sparkle: Float,
    amp: Float
) {
    val u = size.minDimension / 100f
    val cx = size.width / 2f
    val base = Color(bodyInt)
    val body = when (tier) {
        0 -> lerp(base, Color(0xFF7A7A7A), 0.35f)
        2 -> lerp(base, Color.White, 0.12f)
        else -> base
    }
    val darker = lerp(body, Color.Black, 0.18f)
    val belly = if (tier == 0) lerp(Color(ch.bellyColor), Color(0xFF8A8A8A), 0.3f) else Color(ch.bellyColor)
    val dy = (bob - 0.5f) * 4f * u * amp

    // ---------- تخم ----------
    if (stage == 0) {
        val ex = cx
        val ey = 66f * u
        rotate(degrees = (bob - 0.5f) * 12f * amp, pivot = Offset(cx, 90f * u)) {
            drawOval(Color(0x22000000), Offset(ex - 22f * u, 88f * u), Size(44f * u, 7f * u))
            drawOval(lerp(base, Color.White, 0.55f), Offset(ex - 22f * u, ey - 30f * u), Size(44f * u, 56f * u))
            drawCircle(base.copy(alpha = 0.55f), 6f * u, Offset(ex - 8f * u, ey - 6f * u))
            drawCircle(base.copy(alpha = 0.55f), 4.5f * u, Offset(ex + 9f * u, ey + 8f * u))
            drawCircle(base.copy(alpha = 0.55f), 3.5f * u, Offset(ex + 4f * u, ey - 18f * u))
            drawCircle(base.copy(alpha = 0.55f), 5f * u, Offset(ex - 10f * u, ey + 14f * u))
            drawOval(Color.White.copy(alpha = 0.5f), Offset(ex - 14f * u, ey - 22f * u), Size(8f * u, 14f * u))
        }
        return
    }

    val k = when (stage) {
        1 -> 0.5f
        2 -> 0.65f
        3 -> 0.82f
        else -> 1f
    }
    val rx = 30f * u * k
    val ry = 27f * u * k
    val cy = 88f * u - ry + dy

    // سایه زیر پا
    drawOval(Color(0x22000000), Offset(cx - rx * 0.85f, 86f * u), Size(rx * 1.7f, 7f * u))

    // هاله درخشان برای مراقبت عالی
    if (tier == 2) {
        drawCircle(Color(1f, 0.84f, 0.31f, 0.14f + 0.16f * sparkle), rx * 1.65f, Offset(cx, cy))
    }

    // ---------- بال‌ها ----------
    if (ch.hasWings && stage >= 2) {
        val wing = lerp(body, Color.White, 0.3f)
        val flap = (bob - 0.5f) * 0.25f * ry * amp
        triangle(
            Offset(cx - rx * 0.7f, cy - ry * 0.1f),
            Offset(cx - rx * 1.75f, cy - ry * 1.15f + flap),
            Offset(cx - rx * 1.45f, cy + ry * 0.3f), wing
        )
        triangle(
            Offset(cx + rx * 0.7f, cy - ry * 0.1f),
            Offset(cx + rx * 1.75f, cy - ry * 1.15f + flap),
            Offset(cx + rx * 1.45f, cy + ry * 0.3f), wing
        )
    }

    // ---------- دم ----------
    val tailW = 7f * u * k
    when (ch.tailStyle) {
        TailStyle.CAT -> {
            val p = Path()
            p.moveTo(cx + rx * 0.85f, cy + ry * 0.4f)
            p.quadraticBezierTo(cx + rx * 1.7f, cy + ry * 0.1f, cx + rx * 1.25f, cy - ry * 0.8f)
            drawPath(p, body, style = Stroke(width = tailW, cap = StrokeCap.Round))
        }
        TailStyle.DOG -> {
            drawLine(
                body, Offset(cx + rx * 0.85f, cy + ry * 0.5f),
                Offset(cx + rx * 1.3f + (bob - 0.5f) * rx * 0.5f * amp, cy - ry * 0.2f),
                strokeWidth = tailW * 1.3f, cap = StrokeCap.Round
            )
        }
        TailStyle.FLUFF -> {
            drawCircle(Color.White, 9f * u * k, Offset(cx + rx * 0.98f, cy + ry * 0.55f))
        }
        TailStyle.DRAGON -> {
            val p = Path()
            p.moveTo(cx + rx * 0.8f, cy + ry * 0.5f)
            p.quadraticBezierTo(cx + rx * 1.8f, cy + ry * 0.9f, cx + rx * 1.6f, cy + ry * 0.1f)
            drawPath(p, body, style = Stroke(width = tailW * 1.2f, cap = StrokeCap.Round))
            triangle(
                Offset(cx + rx * 1.45f, cy + ry * 0.2f),
                Offset(cx + rx * 1.95f, cy + ry * 0.05f),
                Offset(cx + rx * 1.6f, cy - ry * 0.35f), Color(0xFFFF8E72)
            )
        }
        TailStyle.NONE -> {}
    }

    // ---------- گوش / شاخ (پشت بدن) ----------
    val inner = Color(ch.cheekColor).copy(alpha = 0.8f)
    when (ch.earStyle) {
        EarStyle.POINTY -> {
            triangle(
                Offset(cx - rx * 0.8f, cy - ry * 0.5f), Offset(cx - rx * 0.7f, cy - ry * 1.3f),
                Offset(cx - rx * 0.15f, cy - ry * 0.95f), body
            )
            triangle(
                Offset(cx + rx * 0.8f, cy - ry * 0.5f), Offset(cx + rx * 0.7f, cy - ry * 1.3f),
                Offset(cx + rx * 0.15f, cy - ry * 0.95f), body
            )
            triangle(
                Offset(cx - rx * 0.65f, cy - ry * 0.65f), Offset(cx - rx * 0.62f, cy - ry * 1.08f),
                Offset(cx - rx * 0.32f, cy - ry * 0.88f), inner
            )
            triangle(
                Offset(cx + rx * 0.65f, cy - ry * 0.65f), Offset(cx + rx * 0.62f, cy - ry * 1.08f),
                Offset(cx + rx * 0.32f, cy - ry * 0.88f), inner
            )
        }
        EarStyle.FLOPPY -> {
            drawOval(darker, Offset(cx - rx * 1.12f, cy - ry * 0.75f), Size(rx * 0.55f, ry * 1.0f))
            drawOval(darker, Offset(cx + rx * 0.57f, cy - ry * 0.75f), Size(rx * 0.55f, ry * 1.0f))
        }
        EarStyle.LONG -> {
            drawOval(body, Offset(cx - rx * 0.65f, cy - ry * 2.05f), Size(rx * 0.42f, ry * 1.5f))
            drawOval(body, Offset(cx + rx * 0.23f, cy - ry * 2.05f), Size(rx * 0.42f, ry * 1.5f))
            drawOval(inner, Offset(cx - rx * 0.57f, cy - ry * 1.85f), Size(rx * 0.26f, ry * 1.05f))
            drawOval(inner, Offset(cx + rx * 0.31f, cy - ry * 1.85f), Size(rx * 0.26f, ry * 1.05f))
        }
        EarStyle.HORNS -> {
            val horn = Color(0xFFFFF3B0)
            triangle(
                Offset(cx - rx * 0.55f, cy - ry * 0.8f), Offset(cx - rx * 0.7f, cy - ry * 1.4f),
                Offset(cx - rx * 0.2f, cy - ry * 0.95f), horn
            )
            triangle(
                Offset(cx + rx * 0.55f, cy - ry * 0.8f), Offset(cx + rx * 0.7f, cy - ry * 1.4f),
                Offset(cx + rx * 0.2f, cy - ry * 0.95f), horn
            )
        }
    }

    // ---------- بدن ----------
    drawOval(darker, Offset(cx - rx * 0.7f, cy + ry * 0.78f), Size(rx * 0.55f, ry * 0.3f))
    drawOval(darker, Offset(cx + rx * 0.15f, cy + ry * 0.78f), Size(rx * 0.55f, ry * 0.3f))
    drawOval(body, Offset(cx - rx, cy - ry), Size(rx * 2f, ry * 2f))
    drawOval(belly, Offset(cx - rx * 0.6f, cy + ry * 0.05f), Size(rx * 1.2f, ry * 0.9f))
    if (ch.hasMuzzle) {
        drawOval(belly, Offset(cx - rx * 0.4f, cy - ry * 0.05f), Size(rx * 0.8f, ry * 0.55f))
    }

    // بی‌توجهی: لکه‌های کثیفی و موهای ژولیده
    if (tier == 0) {
        val dirt = Color(0x554E342E)
        drawCircle(dirt, rx * 0.1f, Offset(cx - rx * 0.55f, cy + ry * 0.45f))
        drawCircle(dirt, rx * 0.07f, Offset(cx + rx * 0.5f, cy + ry * 0.6f))
        drawCircle(dirt, rx * 0.08f, Offset(cx + rx * 0.2f, cy - ry * 0.55f))
        val hair = Path()
        hair.moveTo(cx - rx * 0.25f, cy - ry * 0.95f)
        hair.lineTo(cx - rx * 0.1f, cy - ry * 1.25f)
        hair.lineTo(cx + rx * 0.02f, cy - ry * 0.98f)
        hair.lineTo(cx + rx * 0.18f, cy - ry * 1.3f)
        hair.lineTo(cx + rx * 0.28f, cy - ry * 0.95f)
        drawPath(hair, darker, style = Stroke(width = 2.2f * u, cap = StrokeCap.Round))
    }

    // ---------- چشم‌ها ----------
    val exOff = rx * 0.4f
    val ey = cy - ry * 0.15f
    val er = rx * 0.17f
    for (side in listOf(-1f, 1f)) {
        val x = cx + side * exOff
        when (mood) {
            Mood.SLEEPING -> {
                drawArc(
                    Ink, 20f, 140f, false, Offset(x - er, ey - er), Size(er * 2f, er * 2f),
                    style = Stroke(width = 1.8f * u, cap = StrokeCap.Round)
                )
            }
            Mood.SICK -> {
                val s = er * 0.8f
                drawLine(Ink, Offset(x - s, ey - s), Offset(x + s, ey + s), strokeWidth = 1.8f * u, cap = StrokeCap.Round)
                drawLine(Ink, Offset(x - s, ey + s), Offset(x + s, ey - s), strokeWidth = 1.8f * u, cap = StrokeCap.Round)
            }
            else -> {
                drawCircle(Color.White, er, Offset(x, ey))
                drawCircle(Color(eyeInt), er * 0.68f, Offset(x, ey))
                drawCircle(Ink, er * 0.36f, Offset(x, ey))
                drawCircle(Color.White, er * 0.2f, Offset(x - er * 0.25f, ey - er * 0.3f))
                if (mood == Mood.TIRED) {
                    drawRect(body, Offset(x - er * 1.05f, ey - er * 1.05f), Size(er * 2.1f, er * 1.1f))
                    drawLine(Ink, Offset(x - er, ey + 0.05f * er), Offset(x + er, ey + 0.05f * er), strokeWidth = 1.5f * u)
                }
            }
        }
    }
    if (mood == Mood.ANGRY) {
        drawLine(
            Ink, Offset(cx - exOff - er, ey - er * 1.9f), Offset(cx - exOff + er, ey - er * 1.1f),
            strokeWidth = 2f * u, cap = StrokeCap.Round
        )
        drawLine(
            Ink, Offset(cx + exOff + er, ey - er * 1.9f), Offset(cx + exOff - er, ey - er * 1.1f),
            strokeWidth = 2f * u, cap = StrokeCap.Round
        )
    }
    if (tier == 0 && mood != Mood.SLEEPING) {
        drawCircle(Color(0xFF64B5F6), 1.8f * u * k.coerceAtLeast(0.6f), Offset(cx + exOff + er * 1.3f, ey + er * 1.9f))
    }

    // ---------- گونه، بینی و دهان ----------
    val cheek = when (mood) {
        Mood.ANGRY -> Color(0xFFE63946).copy(alpha = 0.6f)
        Mood.SICK -> Color(0xFF9BD97B).copy(alpha = 0.7f)
        else -> Color(ch.cheekColor).copy(alpha = 0.6f)
    }
    drawCircle(cheek, rx * 0.13f, Offset(cx - rx * 0.66f, cy + ry * 0.2f))
    drawCircle(cheek, rx * 0.13f, Offset(cx + rx * 0.66f, cy + ry * 0.2f))
    drawCircle(Color(0xFF5A3A3A), rx * 0.075f, Offset(cx, cy + ry * 0.14f))

    val mr = rx * 0.16f
    val my = cy + ry * 0.3f
    when (mood) {
        Mood.HAPPY -> drawArc(
            Ink, 20f, 140f, false, Offset(cx - mr, my - mr * 1.1f), Size(mr * 2f, mr * 2f),
            style = Stroke(width = 1.8f * u, cap = StrokeCap.Round)
        )
        Mood.HUNGRY -> {
            drawOval(Ink, Offset(cx - mr * 0.7f, my - mr * 0.2f), Size(mr * 1.4f, mr * 1.6f))
            drawOval(Color(0xFFFF8FA3), Offset(cx - mr * 0.4f, my + mr * 0.6f), Size(mr * 0.8f, mr * 0.7f))
        }
        Mood.SLEEPING -> drawCircle(Ink, mr * 0.35f, Offset(cx, my + mr * 0.3f))
        else -> drawArc(
            Ink, 200f, 140f, false, Offset(cx - mr, my), Size(mr * 2f, mr * 2f),
            style = Stroke(width = 1.8f * u, cap = StrokeCap.Round)
        )
    }

    if (ch.whiskers) {
        val w = Color(0xAA444444)
        for (side in listOf(-1f, 1f)) {
            drawLine(w, Offset(cx + side * rx * 0.5f, cy + ry * 0.12f), Offset(cx + side * rx * 1.05f, cy + ry * 0.02f), strokeWidth = 1f * u)
            drawLine(w, Offset(cx + side * rx * 0.5f, cy + ry * 0.24f), Offset(cx + side * rx * 1.05f, cy + ry * 0.3f), strokeWidth = 1f * u)
        }
    }

    // ---------- شکل‌های ویژه مراحل بالا با مراقبت عالی ----------
    if (tier == 2 && stage >= 3) {
        sparkleStar(cx, cy - ry * 0.62f, rx * 0.12f, Color(0xFFFFD54F))
    }
    if (tier == 2 && stage == 4) {
        val crown = Path()
        val by = cy - ry * 1.02f
        crown.moveTo(cx - rx * 0.35f, by)
        crown.lineTo(cx - rx * 0.4f, by - ry * 0.35f)
        crown.lineTo(cx - rx * 0.18f, by - ry * 0.15f)
        crown.lineTo(cx, by - ry * 0.42f)
        crown.lineTo(cx + rx * 0.18f, by - ry * 0.15f)
        crown.lineTo(cx + rx * 0.4f, by - ry * 0.35f)
        crown.lineTo(cx + rx * 0.35f, by)
        crown.close()
        drawPath(crown, Color(0xFFFFC107))
    }
    if (tier == 2) {
        val a = 0.4f + 0.6f * sparkle
        val sc = Color(1f, 0.9f, 0.4f, a)
        sparkleStar(cx - rx * 1.3f, cy - ry * 0.9f, rx * 0.16f * (0.6f + sparkle * 0.6f), sc)
        sparkleStar(cx + rx * 1.35f, cy - ry * 0.6f, rx * 0.14f * (1.2f - sparkle * 0.6f), sc)
        sparkleStar(cx - rx * 1.1f, cy + ry * 0.6f, rx * 0.12f * (1.2f - sparkle * 0.6f), sc)
    }

    // ---------- اکسسوری ----------
    when (accessory) {
        1 -> { // پاپیون
            val bow = Color(0xFFFF4D8D)
            val bx = cx + rx * 0.55f
            val by = cy - ry * 0.85f
            triangle(Offset(bx, by), Offset(bx - rx * 0.3f, by - ry * 0.2f), Offset(bx - rx * 0.3f, by + ry * 0.2f), bow)
            triangle(Offset(bx, by), Offset(bx + rx * 0.3f, by - ry * 0.2f), Offset(bx + rx * 0.3f, by + ry * 0.2f), bow)
            drawCircle(Color(0xFFD81B60), rx * 0.08f, Offset(bx, by))
        }
        2 -> { // کلاه تولد
            val hy = cy - ry * 0.98f
            triangle(Offset(cx - rx * 0.32f, hy), Offset(cx + rx * 0.32f, hy), Offset(cx, hy - ry * 0.85f), Color(0xFF7C4DFF))
            drawCircle(Color(0xFFFFD54F), rx * 0.09f, Offset(cx, hy - ry * 0.85f))
            drawLine(Color(0xFFFFD54F), Offset(cx - rx * 0.2f, hy - ry * 0.25f), Offset(cx + rx * 0.17f, hy - ry * 0.4f), strokeWidth = 1.5f * u)
        }
        3 -> { // عینک
            for (side in listOf(-1f, 1f)) {
                drawCircle(Color(0xFF263238), er * 1.45f, Offset(cx + side * exOff, ey), style = Stroke(width = 1.6f * u))
            }
            drawLine(Color(0xFF263238), Offset(cx - exOff + er * 1.45f, ey), Offset(cx + exOff - er * 1.45f, ey), strokeWidth = 1.6f * u)
        }
    }
}
