package com.example.superahorro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.superahorro.navigation.AppNavigation
import com.example.superahorro.ui.theme.SuperAhorroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperAhorroTheme {
                AppNavigation()
            }
        }
    }
}
