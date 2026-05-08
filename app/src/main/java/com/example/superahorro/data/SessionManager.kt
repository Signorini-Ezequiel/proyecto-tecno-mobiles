package com.example.superahorro.data

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        "superahorro_session",
        Context.MODE_PRIVATE
    )

    init {
        ensureDefaultAccounts()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveSession(email: String) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_EMAIL)
            .apply()
    }

    fun getEmail(): String {
        return preferences.getString(KEY_EMAIL, "invitado@superahorro.app")
            ?: "invitado@superahorro.app"
    }

    fun getCurrentAccount(): MockAccount? {
        val email = preferences.getString(KEY_EMAIL, null) ?: return null
        return getAccount(email)
    }

    fun getAccounts(): List<MockAccount> {
        return preferences.getStringSet(KEY_ACCOUNTS, emptySet())
            .orEmpty()
            .mapNotNull { encodedAccount ->
                val parts = encodedAccount.split(ACCOUNT_SEPARATOR)
                when (parts.size) {
                    2 -> MockAccount(
                        email = parts[0],
                        password = parts[1],
                        username = parts[0].substringBefore("@")
                    )
                    3 -> MockAccount(
                        email = parts[0],
                        password = parts[1],
                        username = parts[2]
                    )
                    else -> null
                }
            }
            .sortedBy { it.email }
    }

    fun getAccount(email: String): MockAccount? {
        return getAccounts().firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    fun accountExists(email: String): Boolean {
        return getAccount(email) != null
    }

    fun validateCredentials(email: String, password: String): Boolean {
        return getAccounts().any {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    fun saveAccount(email: String, password: String, username: String) {
        val accounts = getAccounts()
            .filterNot { it.email.equals(email, ignoreCase = true) }
            .plus(
                MockAccount(
                    email = email.trim(),
                    password = password,
                    username = username.trim()
                )
            )
            .map { it.encode() }
            .toSet()

        preferences.edit()
            .putStringSet(KEY_ACCOUNTS, accounts)
            .apply()
    }

    fun updateUsername(email: String, username: String) {
        val account = getAccount(email) ?: return
        saveAccount(
            email = account.email,
            password = account.password,
            username = username
        )
    }

    fun updatePassword(email: String, newPassword: String) {
        val account = getAccount(email) ?: return
        saveAccount(
            email = account.email,
            password = newPassword,
            username = account.username
        )
    }

    fun isDarkModeEnabled(): Boolean {
        return preferences.getBoolean(KEY_DARK_MODE, true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    private fun ensureDefaultAccounts() {
        val existingAccounts = getAccounts().map { account ->
            val defaultAccount = defaultMockAccounts.firstOrNull {
                it.email.equals(account.email, ignoreCase = true)
            }

            if (defaultAccount != null && account.username == account.email.substringBefore("@")) {
                account.copy(username = defaultAccount.username)
            } else {
                account
            }
        }

        val accountsByEmail = (existingAccounts + defaultMockAccounts)
            .distinctBy { it.email.lowercase() }
            .map { it.encode() }
            .toSet()

        preferences.edit()
            .putStringSet(KEY_ACCOUNTS, accountsByEmail)
            .apply()
    }

    private fun MockAccount.encode(): String {
        return "$email$ACCOUNT_SEPARATOR$password$ACCOUNT_SEPARATOR$username"
    }

    private companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_EMAIL = "email"
        const val KEY_ACCOUNTS = "accounts"
        const val KEY_DARK_MODE = "dark_mode"
        const val ACCOUNT_SEPARATOR = "|"
    }
}
