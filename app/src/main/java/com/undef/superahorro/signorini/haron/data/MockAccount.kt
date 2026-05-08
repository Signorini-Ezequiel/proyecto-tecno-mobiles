package com.undef.superahorro.signorini.haron.data

data class MockAccount(
    val email: String,
    val password: String,
    val username: String
)

val defaultMockAccounts = listOf(
    MockAccount(
        email = "test@superahorro.com",
        password = "123456",
        username = "Usuario Test"
    ),
    MockAccount(
        email = "matias@superahorro.com",
        password = "ahorro123",
        username = "Matias"
    )
)
