package com.mypet.little

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mypet.little.ui.CustomizeScreen
import com.mypet.little.ui.HomeScreen
import com.mypet.little.ui.PetTheme
import com.mypet.little.ui.PetViewModel
import com.mypet.little.ui.Screen
import com.mypet.little.ui.SelectScreen
import com.mypet.little.ui.SettingsScreen
import com.mypet.little.ui.ShopScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    PetApp()
                }
            }
        }
    }
}

@Composable
fun PetApp(vm: PetViewModel = viewModel()) {
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
        }
    }
}
