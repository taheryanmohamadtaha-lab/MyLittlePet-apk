package com.mypet.little

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mypet.little.ui.AboutScreen
import com.mypet.little.ui.CustomizeScreen
import com.mypet.little.ui.HomeScreen
import com.mypet.little.ui.LocalAppTheme
import com.mypet.little.ui.PetTheme
import com.mypet.little.ui.PetViewModel
import com.mypet.little.ui.Screen
import com.mypet.little.ui.SelectScreen
import com.mypet.little.ui.SettingsScreen
import com.mypet.little.ui.ShopScreen
import com.mypet.little.ui.ThemesScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        setContent { PetRoot() }
    }
}

@Composable
fun PetRoot(vm: PetViewModel = viewModel()) {
    val theme = vm.theme
    PetTheme(accent = theme.accent) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalAppTheme provides theme
        ) {
            PetApp(vm)
        }
    }
}

@Composable
fun PetApp(vm: PetViewModel) {
    // نیازهای پت هر ۵ ثانیه به‌روز می‌شوند
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            vm.tick()
        }
    }
    BackHandler(enabled = vm.screen !is Screen.Home && vm.hasPets) {
        vm.go(Screen.Home)
    }
    Box(Modifier.fillMaxSize()) {
        when (val s = vm.screen) {
            is Screen.Home -> HomeScreen(vm)
            is Screen.Select -> SelectScreen(vm, s.slot)
            is Screen.Settings -> SettingsScreen(vm)
            is Screen.Shop -> ShopScreen(vm)
            is Screen.Customize -> CustomizeScreen(vm)
            is Screen.Themes -> ThemesScreen(vm)
            is Screen.About -> AboutScreen(vm)
        }
    }
}
