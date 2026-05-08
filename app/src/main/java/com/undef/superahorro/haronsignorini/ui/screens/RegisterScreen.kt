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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorro.haronsignorini.navigation.AppRoutes
import com.undef.superahorro.haronsignorini.viewmodel.AuthResult

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegister: (String, String, String) -> AuthResult
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val usernameHasError = showErrors && username.trim().length < 3
    val emailHasError = showErrors && !isValidRegisterEmail(email)
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
                            text = "Registro",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Crea una cuenta para empezar a ahorrar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AuthTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Nombre de usuario",
                        placeholder = "Ej: Matias",
                        isError = usernameHasError,
                        supportingText = if (usernameHasError) "Minimo 3 caracteres" else null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null
                            )
                        }
                    )

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "tu@email.com",
                        isError = emailHasError,
                        supportingText = if (emailHasError) "Ingresa un email valido" else null,
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
                        label = "Password",
                        placeholder = "Crea un password",
                        isError = passwordHasError,
                        supportingText = if (passwordHasError) "Minimo 6 caracteres" else null,
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

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showErrors = true
                            authError = null
                            if (
                                username.trim().length < 3 ||
                                !isValidRegisterEmail(email) ||
                                password.length < 6
                            ) return@Button

                            when (val result = onRegister(email, password, username)) {
                                AuthResult.Success -> {
                                    navController.navigate(AppRoutes.Home.route) {
                                        popUpTo(AppRoutes.Landing.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                                is AuthResult.Error -> authError = result.message
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Crear cuenta")
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(AppRoutes.Login.route) },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Ya tengo cuenta")
                    }
                }
            }
        }
    }
}

private fun isValidRegisterEmail(email: String): Boolean {
    return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        .matches(email.trim())
}
