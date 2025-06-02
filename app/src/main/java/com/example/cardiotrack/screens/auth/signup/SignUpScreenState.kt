package com.example.cardiotrack.screens.auth.signup

data class SignUpScreenState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordRepeat: String = "",
    val passwordRepeatError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val loading: Boolean = false,
)