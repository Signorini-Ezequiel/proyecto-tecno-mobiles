package com.example.superahorro.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.superahorro.data.SessionManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var isDarkModeEnabled by mutableStateOf(sessionManager.isDarkModeEnabled())
        private set

    fun updateDarkMode(enabled: Boolean) {
        isDarkModeEnabled = enabled
        sessionManager.setDarkModeEnabled(enabled)
    }
}
