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
import kotlinx.datetime.Instant

class SignUpScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(SignUpScreenState())

    init {
        loadAvailableDoctors()
    }

    fun goToPersonalInfoStep() {
        validateEmailField()
        validatePasswordField()
        validatePasswordRepeatField()
        if (
            state.value.emailError == null &&
            state.value.passwordError == null &&
            state.value.passwordRepeatError == null
        ) {
            state.update { it.copy(step = SignUpScreenStep.PERSONAL_INFO) }
        }
    }

    fun goToMeasurementsStep() {
        validateFirstNameField()
        validateLastNameField()
        validatePeselField()
        validateBirthDateField()
        validateSexField()
        if (
            state.value.firstNameError == null &&
            state.value.lastNameError == null &&
            state.value.peselError == null &&
            state.value.birthDateError == null &&
            state.value.sexError == null
        ) {
            state.update { it.copy(step = SignUpScreenStep.MEASUREMENTS) }
        }
    }

    fun goBackToCredentialsStep() {
        state.update { it.copy(step = SignUpScreenStep.CREDENTIALS) }
    }

    fun goBackToPersonalInfoStep() {
        state.update { it.copy(step = SignUpScreenStep.PERSONAL_INFO) }
    }

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

    fun handleBirthDateChange(birthDate: Instant) {
        state.update { it.copy(birthDate = birthDate, birthDateError = null) }
        hideBirthDateModal()
    }

    fun handleSexChange(sex: Sex) {
        state.update { it.copy(sex = sex, sexError = null) }
    }

    fun onPeselChanged(pesel: String) {
        state.update { it.copy(pesel = pesel, peselError = null) }
    }

    fun changeShowSexDropdown(showSexDropdown: Boolean) {
        state.update { it.copy(showSexDropdown = showSexDropdown) }
    }

    fun showBirthDateModal() {
        state.update { it.copy(showBirthDateModal = true) }
    }

    fun hideBirthDateModal() {
        state.update { it.copy(showBirthDateModal = false) }
    }

    fun changeDoctorDropdown(showDoctorDropdown: Boolean) {
        state.update { it.copy(showDoctorDropdown = showDoctorDropdown) }
    }

    fun handleDoctorChange(doctor: User.Doctor) {
        state.update { it.copy(doctor = doctor, doctorError = null) }
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
                userData.birthDateError == null &&
                userData.peselError == null &&
                userData.doctor != null &&
                userData.doctorError == null
            ) {
                state.update { it.copy(loading = true) }
                val user = authService.signUp(
                    doctor = userData.doctor,
                    email = userData.email,
                    password = userData.password,
                    firstName = userData.firstName,
                    lastName = userData.lastName,
                    sex = userData.sex,
                    birthDate = userData.birthDate,
                    pesel = userData.pesel
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
        validateDoctorField()
        validatePeselField()
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

    private fun validatePeselField() {
        if (state.value.pesel.length != 11 || !state.value.pesel.all { it.isDigit() }) {
            state.update { it.copy(peselError = "PESEL musi mieć dokładnie 11 cyfr") }
            return
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

    private fun validateDoctorField() {
        if (state.value.doctor == null) {
            return state.update { it.copy(doctorError = "Należy wybrać docktora") }
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

    private fun loadAvailableDoctors() {
        viewModelScope.launch {
            val doctors = authService.getAvailableDoctors()
            state.update { it.copy(availableDoctors = doctors) }
        }
    }
}