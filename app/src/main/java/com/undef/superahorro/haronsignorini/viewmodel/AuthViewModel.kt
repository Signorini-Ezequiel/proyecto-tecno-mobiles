package com.undef.superahorro.haronsignorini.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.undef.superahorro.haronsignorini.R
import com.undef.superahorro.haronsignorini.data.MockAccount
import com.undef.superahorro.haronsignorini.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var isLoggedIn by mutableStateOf(sessionManager.isLoggedIn())
        private set

    var userEmail by mutableStateOf(sessionManager.getEmail())
        private set

    var username by mutableStateOf(sessionManager.getCurrentAccount()?.username ?: string(R.string.guest))
        private set

    val quickAccounts: List<MockAccount>
        get() = sessionManager.getAccounts().take(2)

    fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim()
        val validationError = validateEmailAndPassword(normalizedEmail, password)
        if (validationError != null) return AuthResult.Error(validationError)

        if (!sessionManager.accountExists(normalizedEmail)) {
            return AuthResult.Error(string(R.string.account_not_found))
        }

        if (!sessionManager.validateCredentials(normalizedEmail, password)) {
            return AuthResult.Error(string(R.string.password_mismatch))
        }

        saveLoggedSession(normalizedEmail)
        return AuthResult.Success
    }

    fun register(email: String, password: String, username: String): AuthResult {
        val normalizedEmail = email.trim()
        val trimmedUsername = username.trim()
        val validationError = validateEmailAndPassword(normalizedEmail, password)
        if (validationError != null) return AuthResult.Error(validationError)

        if (trimmedUsername.length < 3) {
            return AuthResult.Error(string(R.string.username_min_3_error))
        }

        if (sessionManager.accountExists(normalizedEmail)) {
            return AuthResult.Error(string(R.string.account_already_exists))
        }

        sessionManager.saveAccount(normalizedEmail, password, trimmedUsername)
        saveLoggedSession(normalizedEmail)
        return AuthResult.Success
    }

    fun loginWithMockAccount(account: MockAccount): AuthResult {
        return login(account.email, account.password)
    }

    fun updateUsername(newUsername: String): AuthResult {
        val trimmedUsername = newUsername.trim()
        if (trimmedUsername.length < 3) {
            return AuthResult.Error(string(R.string.username_min_3_error))
        }

        sessionManager.updateUsername(userEmail, trimmedUsername)
        username = trimmedUsername
        return AuthResult.Success
    }

    fun changePassword(currentPassword: String, newPassword: String): AuthResult {
        val validationError = validateEmailAndPassword(userEmail, newPassword)
        if (validationError != null) return AuthResult.Error(validationError)

        if (!sessionManager.validateCredentials(userEmail, currentPassword)) {
            return AuthResult.Error(string(R.string.current_password_mismatch))
        }

        sessionManager.updatePassword(userEmail, newPassword)
        return AuthResult.Success
    }

    fun recoverPassword(email: String, newPassword: String): AuthResult {
        val normalizedEmail = email.trim()
        val validationError = validateEmailAndPassword(normalizedEmail, newPassword)
        if (validationError != null) return AuthResult.Error(validationError)

        if (!sessionManager.accountExists(normalizedEmail)) {
            return AuthResult.Error(string(R.string.account_not_found))
        }

        sessionManager.updatePassword(normalizedEmail, newPassword)
        return AuthResult.Success
    }

    fun logout() {
        sessionManager.clearSession()
        userEmail = sessionManager.getEmail()
        username = string(R.string.guest)
        isLoggedIn = false
    }

    private fun saveLoggedSession(email: String) {
        sessionManager.saveSession(email)
        userEmail = email
        username = sessionManager.getAccount(email)?.username ?: email.substringBefore("@")
        isLoggedIn = true
    }

    private fun validateEmailAndPassword(email: String, password: String): String? {
        if (!EMAIL_REGEX.matches(email)) {
            return string(R.string.invalid_email_period)
        }
        if (password.length < 6) {
            return string(R.string.password_min_6)
        }
        return null
    }

    private fun string(resId: Int): String {
        return getApplication<Application>().getString(resId)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
