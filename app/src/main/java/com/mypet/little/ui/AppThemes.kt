package com.mypet.little.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** تم‌های رنگی برنامه. برای اضافه کردن تم جدید فقط یک خط به لیست اضافه کن. */
data class AppTheme(
    val id: String,
    val nameFa: String,
    val emoji: String,
    val top: Color,
    val bottom: Color,
    val accent: Color
)

object AppThemes {
    val all = listOf(
        AppTheme("candy", "آبنباتی", "🍭", Color(0xFFFFF0F7), Color(0xFFE8F4FF), Color(0xFFFF6FA5)),
        AppTheme("sky", "آسمان آبی", "☁️", Color(0xFFE3F2FF), Color(0xFFF4FBFF), Color(0xFF3A8DFF)),
        AppTheme("forest", "جنگل سبز", "🌳", Color(0xFFE6F6DF), Color(0xFFF7FFEA), Color(0xFF43A047)),
        AppTheme("sunset", "غروب نارنجی", "🌇", Color(0xFFFFE9D2), Color(0xFFFFF6E5), Color(0xFFFF8A3D)),
        AppTheme("galaxy", "کهکشان", "🌌", Color(0xFFE6DDFB), Color(0xFFF6F0FF), Color(0xFF7E57C2)),
        AppTheme("sea", "دریای فیروزه‌ای", "🐚", Color(0xFFD9F5F2), Color(0xFFF0FFFD), Color(0xFF00A99D)),
        AppTheme("choco", "شکلاتی", "🍫", Color(0xFFF3E4D8), Color(0xFFFFF7F0), Color(0xFF8D5A3B)),
        AppTheme("lemon", "لیمویی", "🍋", Color(0xFFFFF8C9), Color(0xFFFFFFEE), Color(0xFFE0A800))
    )

    fun get(id: String): AppTheme = all.firstOrNull { it.id == id } ?: all[0]
}

val LocalAppTheme = compositionLocalOf { AppThemes.all[0] }

@Composable
fun themeBrush(): Brush {
    val t = LocalAppTheme.current
    return Brush.verticalGradient(listOf(t.top, t.bottom))
}

@Composable
fun accentColor(): Color = LocalAppTheme.current.accent
