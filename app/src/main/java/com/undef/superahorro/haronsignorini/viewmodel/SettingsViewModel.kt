package com.undef.superahorro.haronsignorini.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.undef.superahorro.haronsignorini.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    var isDarkModeEnabled by mutableStateOf(sessionManager.isDarkModeEnabled())
        private set

    fun updateDarkMode(enabled: Boolean) {
        isDarkModeEnabled = enabled
        sessionManager.setDarkModeEnabled(enabled)
    }
}
