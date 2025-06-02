package com.example.cardiotrack.screens.auth.signin

data class SignInScreenState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val loading: Boolean = false,
)