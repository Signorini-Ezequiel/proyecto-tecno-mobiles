package com.undef.superahorro.haronsignorini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorro.haronsignorini.data.MockAccount
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.viewmodel.AuthResult

@Composable
fun LoginScreen(
    navController: NavController,
    quickAccounts: List<MockAccount>,
    onLogin: (String, String) -> AuthResult,
    onQuickLogin: (MockAccount) -> AuthResult,
    onPasswordRecovery: (String, String) -> AuthResult
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryMessage by remember { mutableStateOf<String?>(null) }

    val emailHasError = showErrors && !isValidEmail(email)
    val passwordHasError = showErrors && password.length < 6

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(LoginStringIds.loginTitle),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(LoginStringIds.loginSubtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(LoginStringIds.email),
                        placeholder = stringResource(LoginStringIds.emailPlaceholder),
                        isError = emailHasError,
                        supportingText = if (emailHasError) stringResource(LoginStringIds.invalidEmail) else null,
                        keyboardType = KeyboardType.Email,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = null
                            )
                        }
                    )

                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(LoginStringIds.password),
                        placeholder = stringResource(LoginStringIds.passwordPlaceholder),
                        isError = passwordHasError,
                        supportingText = if (passwordHasError) stringResource(LoginStringIds.minimum6Chars) else null,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null
                            )
                        }
                    )

                    authError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    recoveryMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showErrors = true
                            authError = null
                            if (!isValidEmail(email) || password.length < 6) return@Button

                            when (val result = onLogin(email, password)) {
                                AuthResult.Success -> navigateToHome(navController)
                                is AuthResult.Error -> authError = result.message
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(LoginStringIds.loginButton))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(LoginStringIds.loginQuickAccounts),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        quickAccounts.forEach { account ->
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    authError = null
                                    when (val result = onQuickLogin(account)) {
                                        AuthResult.Success -> navigateToHome(navController)
                                        is AuthResult.Error -> authError = result.message
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(stringResource(LoginStringIds.loginAs, account.email))
                            }
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(AppRoutes.Register.route) },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(LoginStringIds.goToRegister))
                    }

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            recoveryMessage = null
                            showRecoveryDialog = true
                        }
                    ) {
                        Text(stringResource(LoginStringIds.forgotPassword))
                    }
                }
            }
        }
    }

    if (showRecoveryDialog) {
        PasswordRecoveryDialog(
            initialEmail = email,
            onRecover = onPasswordRecovery,
            onRecovered = {
                recoveryMessage = it
                showRecoveryDialog = false
            },
            onDismiss = { showRecoveryDialog = false }
        )
    }
}

@Composable
private fun PasswordRecoveryDialog(
    initialEmail: String,
    onRecover: (String, String) -> AuthResult,
    onRecovered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var recoveryEmail by remember { mutableStateOf(initialEmail) }
    var newPassword by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }
    var recoveryError by remember { mutableStateOf<String?>(null) }
    val successMessage = stringResource(LoginStringIds.passwordRecoverySuccess)
    val newPasswordsDoNotMatch = stringResource(LoginStringIds.newPasswordsDoNotMatch)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(LoginStringIds.recoverPassword)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthTextField(
                    value = recoveryEmail,
                    onValueChange = { recoveryEmail = it },
                    label = stringResource(LoginStringIds.email),
                    placeholder = stringResource(LoginStringIds.emailPlaceholder),
                    isError = false,
                    supportingText = null,
                    keyboardType = KeyboardType.Email
                )
                AuthTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(LoginStringIds.newPassword),
                    placeholder = stringResource(LoginStringIds.createPasswordPlaceholder),
                    isError = false,
                    supportingText = null,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                AuthTextField(
                    value = repeatedPassword,
                    onValueChange = { repeatedPassword = it },
                    label = stringResource(LoginStringIds.repeatNewPassword),
                    placeholder = stringResource(LoginStringIds.createPasswordPlaceholder),
                    isError = false,
                    supportingText = null,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                recoveryError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword != repeatedPassword) {
                        recoveryError = newPasswordsDoNotMatch
                        return@TextButton
                    }
                    when (val result = onRecover(recoveryEmail, newPassword)) {
                        AuthResult.Success -> onRecovered(successMessage)
                        is AuthResult.Error -> recoveryError = result.message
                    }
                }
            ) {
                Text(stringResource(LoginStringIds.savePassword))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(LoginStringIds.cancel))
            }
        }
    )
}

private fun navigateToHome(navController: NavController) {
    navController.navigate(AppRoutes.Home.route) {
        popUpTo(AppRoutes.Landing.route) {
            inclusive = true
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        .matches(email.trim())
}
