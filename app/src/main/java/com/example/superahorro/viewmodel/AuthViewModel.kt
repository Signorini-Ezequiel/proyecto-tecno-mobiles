package com.example.superahorro.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.superahorro.data.MockAccount
import com.example.superahorro.data.SessionManager

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var isLoggedIn by mutableStateOf(sessionManager.isLoggedIn())
        private set

    var userEmail by mutableStateOf(sessionManager.getEmail())
        private set

    var username by mutableStateOf(sessionManager.getCurrentAccount()?.username ?: "Invitado")
        private set

    val quickAccounts: List<MockAccount>
        get() = sessionManager.getAccounts().take(2)

    fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim()
        val validationError = validateEmailAndPassword(normalizedEmail, password)
        if (validationError != null) return AuthResult.Error(validationError)

        if (!sessionManager.accountExists(normalizedEmail)) {
            return AuthResult.Error("No existe una cuenta con ese email.")
        }

        if (!sessionManager.validateCredentials(normalizedEmail, password)) {
            return AuthResult.Error("La contrasena no coincide.")
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
            return AuthResult.Error("El nombre de usuario debe tener al menos 3 caracteres.")
        }

        if (sessionManager.accountExists(normalizedEmail)) {
            return AuthResult.Error("Ya existe una cuenta con ese email.")
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
            return AuthResult.Error("El nombre de usuario debe tener al menos 3 caracteres.")
        }

        sessionManager.updateUsername(userEmail, trimmedUsername)
        username = trimmedUsername
        return AuthResult.Success
    }

    fun changePassword(currentPassword: String, newPassword: String): AuthResult {
        val validationError = validateEmailAndPassword(userEmail, newPassword)
        if (validationError != null) return AuthResult.Error(validationError)

        if (!sessionManager.validateCredentials(userEmail, currentPassword)) {
            return AuthResult.Error("La contrasena actual no coincide.")
        }

        sessionManager.updatePassword(userEmail, newPassword)
        return AuthResult.Success
    }

    fun logout() {
        sessionManager.clearSession()
        userEmail = sessionManager.getEmail()
        username = "Invitado"
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
            return "Ingresa un email valido."
        }
        if (password.length < 6) {
            return "La contrasena debe tener al menos 6 caracteres."
        }
        return null
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
