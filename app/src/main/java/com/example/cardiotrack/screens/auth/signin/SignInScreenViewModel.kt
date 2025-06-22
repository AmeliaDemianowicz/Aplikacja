package com.example.cardiotrack.screens.auth.signin

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.auth.signup.SignUpScreen
import com.example.cardiotrack.screens.doctor.dashboard.DoctorDashboardScreen
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.services.auth.AuthError
import com.example.cardiotrack.services.auth.AuthService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(SignInScreenState())

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
        navController.navigate(SignUpScreen) {
            launchSingleTop = true
            popUpTo<SignInScreen> { inclusive = true }
        }
    }

    private fun validateForm() {
        validateEmailField()
        validatePasswordField()
    }

    private fun validateEmailField() {
        if (state.value.email.isBlank()) {
            return state.update { it.copy(emailError = "E-mail nie może być pusty") }
        }
        if (!EMAIL_ADDRESS.matcher(state.value.email).matches()) {
            return state.update { it.copy(emailError = "Nieprawidłowy e-mail") }
        }
    }

    private fun validatePasswordField() {
        if (state.value.password.isBlank()) {
            return state.update { it.copy(passwordError = "Hasło nie może być puste") }
        }
        if (state.value.password.length < 8) {
            return state.update { it.copy(passwordError = "Hasło musi mieć co najmniej 8 znaków") }
        }
    }

    private fun handleError(error: Throwable) {
        state.update { it.copy(loading = false) }
        when (error) {
            is AuthError.UserNotFound -> state.update { it.copy(emailError = "Użytkownik z takim e-mailem nie istnieje") }
            else -> throw error
        }
    }

    private fun redirectToNextScreen(user: User) {
        val nextScreen = when (user) {
            is User.Doctor -> DoctorDashboardScreen(user)
            is User.Patient -> PatientDashboardScreen(user)
        }

        navController.navigate(nextScreen) {
            launchSingleTop = true
            popUpTo<SignInScreen> { inclusive = true }
        }
    }
}
