package com.example.cardiotrack.screens.auth.signup

import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import kotlinx.datetime.Instant
import java.time.LocalTime

enum class SignUpScreenStep {
    CREDENTIALS,
    PERSONAL_INFO,
    MEASUREMENTS
}

data class SignUpScreenState(
    val step: SignUpScreenStep = SignUpScreenStep.MEASUREMENTS,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordRepeat: String = "",
    val passwordRepeatError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val pesel: String = "",
    val peselError: String? = null,
    val lastNameError: String? = null,
    val sex: Sex? = null,
    val sexError: String? = null,
    val showSexDropdown: Boolean = false,
    val birthDate: Instant? = null,
    val birthDateError: String? = null,
    val showBirthDateModal: Boolean = false,
    val availableDoctors: List<User.Doctor> = emptyList(),
    val doctor: User.Doctor? = null,
    val doctorError: String? = null,
    val showDoctorDropdown: Boolean = false,
    val dailyMeasurementRemindersCount: Int = 1,
    val dailyMeasurementRemindersCountError: String? = null,
    val showDailyMeasurementCountDropdown: Boolean = false,
    val dailyMeasurementReminders: List<LocalTime?> = listOf(null),
    val dailyMeasurementRemindersErrors: List<String?> = listOf(null),
    val showDailyMeasurementRemindersTimeModals: List<Boolean> = listOf(false),
    val loading: Boolean = false,
)