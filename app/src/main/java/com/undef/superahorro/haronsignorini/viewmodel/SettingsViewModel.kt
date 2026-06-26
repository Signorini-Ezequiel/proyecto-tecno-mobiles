package com.undef.superahorro.haronsignorini.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.undef.superahorro.haronsignorini.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var isDarkModeEnabled by mutableStateOf(sessionManager.isDarkModeEnabled())
        private set

    fun updateDarkMode(enabled: Boolean) {
        isDarkModeEnabled = enabled
        sessionManager.setDarkModeEnabled(enabled)
    }
}
