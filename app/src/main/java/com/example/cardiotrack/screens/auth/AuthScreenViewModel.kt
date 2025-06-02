package com.example.cardiotrack.screens.auth

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.auth.AuthError
import com.example.cardiotrack.services.auth.AuthService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(AuthScreenState())

    fun handleEmailChange(email: String) {
        state.update { it.copy(email = email, emailError = null) }
    }

    fun handlePasswordChange(password: String) {
        state.update { it.copy(password = password, passwordError = null) }
    }

    fun handleSignIn() {
        viewModelScope.launch(CoroutineExceptionHandler { _, error -> handleError(error) }) {
            state.update { it.copy(emailError = null, passwordError = null) }
            validateForm()
            if (state.value.emailError == null && state.value.passwordError == null) {
                state.update { it.copy(loading = true) }
                val user = authService.signIn(state.value.email, state.value.password)
                state.update { it.copy(loading = false) }
                redirectToNextScreen(user)
            }
        }
    }

    fun handleSignUp() {
        viewModelScope.launch(CoroutineExceptionHandler { _, error -> handleError(error) }) {
            state.update { it.copy(emailError = null, passwordError = null) }
            validateForm()
            if (state.value.emailError == null && state.value.passwordError == null) {
                state.update { it.copy(loading = true) }
                state.update { it.copy(loading = false) }
            }
        }
    }

    private fun validateForm() {
        if (!EMAIL_ADDRESS.matcher(state.value.email).matches()) {
            state.update { it.copy(emailError = "Nieprawidłowy e-mail") }
        }
        if (state.value.email.isBlank()) {
            state.update { it.copy(emailError = "E-mail nie może być pusty") }
        }
        if (state.value.password.length < 8) {
            state.update { it.copy(passwordError = "Hasło musi mieć co najmniej 8 znaków") }
        }
        if (state.value.password.isBlank()) {
            state.update { it.copy(passwordError = "Hasło nie może być puste") }
        }
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is AuthError.UserAlreadyExists -> state.update { it.copy(emailError = "Użytkownik z takim e-mailem już istnieje") }
            is AuthError.UserNotFound -> state.update { it.copy(emailError = "Użytkownik z takim e-mailem nie istnieje") }
            else -> throw error
        }
        state.update { it.copy(loading = false) }
    }

    private fun redirectToNextScreen(user: User?) {
    }
}