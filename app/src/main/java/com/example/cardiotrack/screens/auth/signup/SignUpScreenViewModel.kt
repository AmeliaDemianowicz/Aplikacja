package com.example.cardiotrack.screens.auth.signup

import android.content.Context
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cardiotrack.CardioTrackNotificationWorker
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
import java.time.LocalTime
import java.util.concurrent.TimeUnit
/**
 * ViewModel zarządzający stanem i logiką rejestracji użytkownika.
 *
 * @property authService Serwis uwierzytelniania obsługujący rejestrację.
 * @property navController Kontroler nawigacji do zmiany ekranów.
 */
class SignUpScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    /** Aktualny stan ekranu rejestracji. */
    val state = MutableStateFlow(SignUpScreenState())

    init {
        loadAvailableDoctors()
    }
    /** Przechodzi do kroku wprowadzania danych osobowych po walidacji danych logowania. */
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
    /** Przechodzi do kroku wprowadzania pomiarów po walidacji danych osobowych. */
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
    /** Powrót do kroku wprowadzania danych logowania. */
    fun goBackToCredentialsStep() {
        state.update { it.copy(step = SignUpScreenStep.CREDENTIALS) }
    }
    /** Powrót do kroku wprowadzania danych osobowych. */
    fun goBackToPersonalInfoStep() {
        state.update { it.copy(step = SignUpScreenStep.PERSONAL_INFO) }
    }
    /** Obsługuje zmianę wartości pola email. */
    fun handleEmailChange(email: String) {
        state.update { it.copy(email = email, emailError = null) }
    }

    /** Obsługuje zmianę wartości pola hasła. */
    fun handlePasswordChange(password: String) {
        state.update { it.copy(password = password, passwordError = null) }
    }
    /** Obsługuje zmianę wartości pola powtórzonego hasła. */
    fun handlePasswordRepeatChange(passwordRepeat: String) {
        state.update { it.copy(passwordRepeat = passwordRepeat, passwordRepeatError = null) }
    }

    /** Obsługuje zmianę wartości pola imienia. */
    fun handleFirstNameChange(firstName: String) {
        state.update { it.copy(firstName = firstName, firstNameError = null) }
    }

    /** Obsługuje zmianę wartości pola nazwiska. */
    fun handleLastNameChange(lastName: String) {
        state.update { it.copy(lastName = lastName, lastNameError = null) }
    }
    /** Obsługuje zmianę wartości pola daty urodzenia. */
    fun handleBirthDateChange(birthDate: Instant) {
        state.update { it.copy(birthDate = birthDate, birthDateError = null) }
        hideBirthDateModal()
    }

    /** Obsługuje zmianę wyboru płci. */
    fun handleSexChange(sex: Sex) {
        state.update { it.copy(sex = sex, sexError = null) }
    }
    /** Obsługuje zmianę wartości pola PESEL. */
    fun onPeselChanged(pesel: String) {
        state.update { it.copy(pesel = pesel, peselError = null) }
    }

    /** Kontroluje widoczność rozwijanego menu wyboru płci. */
    fun changeShowSexDropdown(showSexDropdown: Boolean) {
        state.update { it.copy(showSexDropdown = showSexDropdown) }
    }
    /** Pokazuje modal wyboru daty urodzenia. */
    fun showBirthDateModal() {
        state.update { it.copy(showBirthDateModal = true) }
    }
    /** Ukrywa modal wyboru daty urodzenia. */
    fun hideBirthDateModal() {
        state.update { it.copy(showBirthDateModal = false) }
    }

    /** Kontroluje widoczność rozwijanego menu wyboru lekarza. */
    fun changeDoctorDropdown(showDoctorDropdown: Boolean) {
        state.update { it.copy(showDoctorDropdown = showDoctorDropdown) }
    }
    /** Obsługuje zmianę wyboru lekarza. */
    fun handleDoctorChange(doctor: User.Doctor) {
        state.update { it.copy(doctor = doctor, doctorError = null) }
    }

    /** Kontroluje widoczność rozwijanego menu liczby powiadomień pomiarów dziennie. */
    fun changeDailyMeasurementRemindersCountDropdown(showDailyMeasurementCountDropdown: Boolean) {
        state.update { it.copy(showDailyMeasurementCountDropdown = showDailyMeasurementCountDropdown) }
    }
    /**
     * Obsługuje zmianę liczby powiadomień/pomiarów dziennie.
     *
     * @param count Nowa liczba powiadomień.
     */
    fun handleDailyMeasurementRemindersCountChange(count: Int) {
        state.update {
            it.copy(
                dailyMeasurementRemindersCount = count,
                dailyMeasurementRemindersCountError = null,
                dailyMeasurementReminders = List(count) { index ->
                    it.dailyMeasurementReminders.getOrNull(index)
                },
                dailyMeasurementRemindersErrors = List(count) { null },
                showDailyMeasurementRemindersTimeModals = List(count) { false }
            )
        }
    }
    /**
     * Obsługuje zmianę godziny konkretnego przypomnienia pomiaru.
     *
     * @param reminderIndex Indeks przypomnienia do zmiany.
     * @param hour Godzina przypomnienia.
     * @param minute Minuta przypomnienia.
     */
    fun handleDailyMeasurementReminderChange(reminderIndex: Int, hour: Int, minute: Int) {
        state.update {
            it.copy(
                dailyMeasurementReminders = it.dailyMeasurementReminders
                    .toMutableList()
                    .also { it[reminderIndex] = LocalTime.of(hour, minute) },
                dailyMeasurementRemindersErrors = it.dailyMeasurementRemindersErrors
                    .toMutableList()
                    .also { it[reminderIndex] = null }
            )
        }
        hideDailyMeasurementRemindersTimeModals(reminderIndex)
    }
    /** Pokazuje modal wyboru godziny przypomnienia pomiaru o podanym indeksie. */
    fun showDailyMeasurementReminderTimeModal(reminderIndex: Int) {
        state.update {
            it.copy(
                showDailyMeasurementRemindersTimeModals = it.showDailyMeasurementRemindersTimeModals.toMutableList()
                    .also { it[reminderIndex] = true }
            )
        }
    }
    /** Ukrywa modal wyboru godziny przypomnienia pomiaru o podanym indeksie. */
    fun hideDailyMeasurementRemindersTimeModals(reminderIndex: Int) {
        state.update {
            it.copy(
                showDailyMeasurementRemindersTimeModals = it.showDailyMeasurementRemindersTimeModals.toMutableList()
                    .also { it[reminderIndex] = false }
            )
        }
    }
    /**
     * Obsługuje proces rejestracji użytkownika.
     *
     * @param context Kontekst aplikacji, potrzebny do zarządzania powiadomieniami.
     */
    fun handleSignUp(context: Context) {
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


                WorkManager.getInstance(context).cancelAllWorkByTag("measurement_notification")

                userData.dailyMeasurementReminders.filterNotNull().forEach {
                    val delay = CardioTrackNotificationWorker.initialDelay(it.hour, it.minute)

                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                        "measurement_notification_work_${it.hour}_${it.minute}",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        PeriodicWorkRequestBuilder<CardioTrackNotificationWorker>(1, TimeUnit.DAYS)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .addTag("measurement_notification")
                            .build()
                    )
                }

                state.update { it.copy(loading = false) }
                redirectToNextScreen(user)
            }
        }
    }
    /** Przechodzi do ekranu logowania. */
    fun handleSignIn() {
        navController.navigate(SignInScreen) {
            launchSingleTop = true
            popUpTo<SignUpScreen> { inclusive = true }
        }
    }
    /** Waliduje cały formularz rejestracji. */
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
        validateDailyMeasurementRemindersCountField()
        validateDailyMeasurementRemindersFields()
    }

    /** Waliduje pole email. */
    private fun validateEmailField() {
        if (state.value.email.isBlank()) {
            return state.update { it.copy(emailError = "E-mail nie może być pusty") }
        }
        if (!EMAIL_ADDRESS.matcher(state.value.email).matches()) {
            return state.update { it.copy(emailError = "Nieprawidłowy e-mail") }
        }
    }
    /** Waliduje pole hasła. */

    private fun validatePasswordField() {
        if (state.value.password.isBlank()) {
            return state.update { it.copy(passwordError = "Hasło nie może być puste") }
        }
        if (state.value.password.length < 8) {
            return state.update { it.copy(passwordError = "Hasło musi mieć co najmniej 8 znaków") }
        }
    }
    /** Waliduje pole PESEL. */
    private fun validatePeselField() {
        if (state.value.pesel.length != 11 || !state.value.pesel.all { it.isDigit() }) {
            state.update { it.copy(peselError = "PESEL musi mieć dokładnie 11 cyfr") }
            return
        }

    }
    /** Waliduje pole powtórzonego hasła. */
    private fun validatePasswordRepeatField() {
        if (state.value.passwordRepeat != state.value.password) {
            return state.update { it.copy(passwordRepeatError = "Hasła nie są takie same") }
        }
    }
    /** Waliduje pole imienia. */
    private fun validateFirstNameField() {
        if (state.value.firstName.isBlank()) {
            return state.update { it.copy(firstNameError = "Imie nie może być puste") }
        }
    }
    /** Waliduje pole nazwiska. */
    private fun validateLastNameField() {
        if (state.value.lastName.isBlank()) {
            return state.update { it.copy(lastNameError = "Nazwisko nie może być puste") }
        }
    }

    /** Waliduje pole daty urodzenia. */
    private fun validateBirthDateField() {
        if (state.value.birthDate == null) {
            return state.update { it.copy(birthDateError = "Data urodzenia nie może być pusta") }
        }
    }
    /** Waliduje pole płci. */
    private fun validateSexField() {
        if (state.value.sex == null) {
            return state.update { it.copy(sexError = "Płeć nie może być pusta") }
        }
    }
    /** Waliduje wybór lekarza. */
    private fun validateDoctorField() {
        if (state.value.doctor == null) {
            return state.update { it.copy(doctorError = "Należy wybrać doktora") }
        }
    }
    /** Waliduje liczbę powiadomień dziennie. */
    private fun validateDailyMeasurementRemindersCountField() {
        if (state.value.dailyMeasurementRemindersCount !in 1..3) {
            return state.update { it.copy(dailyMeasurementRemindersCountError = "Należy wybrać od 1 do 3 powiadomień") }
        }
    }

    /** Waliduje czasy powiadomień pomiarów. */
    private fun validateDailyMeasurementRemindersFields() {
        return state.update {
            it.copy(dailyMeasurementRemindersErrors = it.dailyMeasurementReminders.map {
                if (it == null) "Należy wybrać godzine pomiaru" else null
            })
        }
    }
    /** Obsługuje błąd w trakcie rejestracji. */
    private fun handleError(error: Throwable) {
        state.update { it.copy(loading = false) }
        when (error) {
            is AuthError.UserAlreadyExists -> state.update {
                it.copy(
                    step = SignUpScreenStep.CREDENTIALS,
                    emailError = "Użytkownik z takim e-mailem już istnieje"
                )
            }

            is AuthError.PeselAlreadyExists -> state.update {
                it.copy(
                    step = SignUpScreenStep.PERSONAL_INFO,
                    peselError = "Użytkownik z takim numerem PESEL już istnieje"
                )
            }

            else -> throw error
        }
    }
    /** Przekierowuje użytkownika po rejestracji na odpowiedni ekran. */
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
    /** Ładuje dostępnych lekarzy z serwisu uwierzytelniania. */
    private fun loadAvailableDoctors() {
        viewModelScope.launch {
            val doctors = authService.getAvailableDoctors()
            state.update { it.copy(availableDoctors = doctors) }
        }
    }
}