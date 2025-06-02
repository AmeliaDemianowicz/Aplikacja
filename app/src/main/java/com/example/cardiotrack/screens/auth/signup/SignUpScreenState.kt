package com.example.cardiotrack.screens.auth.signup

import com.example.cardiotrack.domain.Sex
import kotlinx.datetime.LocalDate

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
    val sex: Sex? = null,
    val sexError: String? = null,
    val birthDate: LocalDate? = null,
    val birthDateError: String? = null,
    val loading: Boolean = false,
)