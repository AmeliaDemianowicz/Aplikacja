package com.example.cardiotrack.screens.auth.signup

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.auth.signin.SignInScreen
import com.example.cardiotrack.screens.doctor.dashboard.DoctorDashboardScreen
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.services.auth.AuthError
import com.example.cardiotrack.services.auth.AuthService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SignUpScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(SignUpScreenState())

    fun handleEmailChange(email: String) {
        state.update { it.copy(email = email, emailError = null) }
    }

    fun handlePasswordChange(password: String) {
        state.update { it.copy(password = password, passwordError = null) }
    }

    fun handlePasswordRepeatChange(passwordRepeat: String) {
        state.update { it.copy(passwordRepeat = passwordRepeat, passwordRepeatError = null) }
    }

    fun handleFirstNameChange(firstName: String) {
        state.update { it.copy(firstName = firstName, firstNameError = null) }
    }

    fun handleLastNameChange(lastName: String) {
        state.update { it.copy(lastName = lastName, lastNameError = null) }
    }

    fun handleBirthDateChange(birthDate: LocalDate) {
        state.update { it.copy(birthDate = birthDate, birthDateError = null) }
    }

    fun handleSexChange(sex: Sex) {
        state.update { it.copy(sex = sex, sexError = null) }
    }

    fun handleSignUp() {
        viewModelScope.launch(CoroutineExceptionHandler { _, error -> handleError(error) }) {
            state.update { it.copy(emailError = null, passwordError = null) }
            validateForm()
            val userData = state.value
            if (
                userData.emailError == null &&
                userData.passwordError == null &&
                userData.passwordRepeatError == null &&
                userData.firstNameError == null &&
                userData.lastNameError == null &&
                userData.sex != null &&
                userData.sexError == null &&
                userData.birthDate != null &&
                userData.birthDateError == null
            ) {
                state.update { it.copy(loading = true) }
                val user = authService.signUp(
                    email = userData.email,
                    password = userData.password,
                    firstName = userData.firstName,
                    lastName = userData.lastName,
                    sex = userData.sex,
                    birthDate = userData.birthDate
                )
                state.update { it.copy(loading = false) }
                redirectToNextScreen(user)
            }
        }
    }

    fun handleSignIn() {
        navController.navigate(SignInScreen) {
            launchSingleTop = true
            popUpTo<SignUpScreen> { inclusive = true }
        }
    }

    private fun validateForm() {
        validateEmailField()
        validatePasswordField()
        validatePasswordRepeatField()
        validateFirstNameField()
        validateLastNameField()
        validateBirthDateField()
        validateSexField()
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

    private fun validatePasswordRepeatField() {
        if (state.value.passwordRepeat != state.value.password) {
            return state.update { it.copy(passwordRepeatError = "Hasła nie są takie same") }
        }
    }

    private fun validateFirstNameField() {
        if (state.value.firstName.isBlank()) {
            return state.update { it.copy(firstNameError = "Imie nie może być puste") }
        }
    }

    private fun validateLastNameField() {
        if (state.value.lastName.isBlank()) {
            return state.update { it.copy(lastNameError = "Nazwisko nie może być puste") }
        }
    }

    private fun validateBirthDateField() {
        if (state.value.birthDate == null) {
            return state.update { it.copy(birthDateError = "Data urodzenia nie może być pusta") }
        }
    }

    private fun validateSexField() {
        if (state.value.sex == null) {
            return state.update { it.copy(sexError = "Płeć nie może być pusta") }
        }
    }

    private fun handleError(error: Throwable) {
        state.update { it.copy(loading = false) }
        when (error) {
            is AuthError.UserAlreadyExists -> state.update { it.copy(emailError = "Użytkownik z takim e-mailem już istnieje") }
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