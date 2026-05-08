package com.example.superahorro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superahorro.navigation.AppNavigation
import com.example.superahorro.ui.theme.SuperAhorroTheme
import com.example.superahorro.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkModeEnabled = settingsViewModel.isDarkModeEnabled

            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkModeEnabled) {
                        SystemBarStyle.dark(scrim = 0xFF07110D.toInt())
                    } else {
                        SystemBarStyle.light(
                            scrim = 0xFFF5FBF7.toInt(),
                            darkScrim = 0xFFF5FBF7.toInt()
                        )
                    },
                    navigationBarStyle = if (isDarkModeEnabled) {
                        SystemBarStyle.dark(scrim = 0xFF07110D.toInt())
                    } else {
                        SystemBarStyle.light(
                            scrim = 0xFFF5FBF7.toInt(),
                            darkScrim = 0xFFF5FBF7.toInt()
                        )
                    }
                )
            }

            SuperAhorroTheme(darkTheme = isDarkModeEnabled) {
                AppNavigation(
                    darkModeEnabled = isDarkModeEnabled,
                    onDarkModeChange = settingsViewModel::updateDarkMode
                )
            }
        }
    }
}
