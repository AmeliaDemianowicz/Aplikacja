package com.example.cardiotrack.screens.auth.signup

import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import kotlinx.datetime.Instant
import java.time.LocalTime
/**
 * Etapy procesu rejestracji użytkownika.
 */
enum class SignUpScreenStep {
    /** Etap wprowadzania danych logowania (email, hasło). */
    CREDENTIALS,
    /** Etap wprowadzania danych osobowych (imię, nazwisko, PESEL, płeć, data urodzenia). */
    PERSONAL_INFO,
    /** Etap ustawiania pomiarów i powiadomień. */
    MEASUREMENTS
}
/**
 * Stan ekranu rejestracji użytkownika.
 *
 * @property step Aktualny krok rejestracji.
 * @property email Wprowadzony adres e-mail.
 * @property emailError Komunikat błędu dotyczący e-maila (jeśli występuje).
 * @property password Wprowadzone hasło.
 * @property passwordError Komunikat błędu dotyczący hasła (jeśli występuje).
 * @property passwordRepeat Powtórzone hasło.
 * @property passwordRepeatError Komunikat błędu dotyczący powtórzonego hasła (jeśli występuje).
 * @property firstName Imię użytkownika.
 * @property firstNameError Komunikat błędu dotyczący imienia (jeśli występuje).
 * @property lastName Nazwisko użytkownika.
 * @property lastNameError Komunikat błędu dotyczący nazwiska (jeśli występuje).
 * @property pesel Numer PESEL użytkownika.
 * @property peselError Komunikat błędu dotyczący PESELu (jeśli występuje).
 * @property sex Płeć użytkownika (MAN/WOMAN).
 * @property sexError Komunikat błędu dotyczący wyboru płci (jeśli występuje).
 * @property showSexDropdown Flaga określająca, czy rozwijane menu płci jest widoczne.
 * @property birthDate Data urodzenia użytkownika.
 * @property birthDateError Komunikat błędu dotyczący daty urodzenia (jeśli występuje).
 * @property showBirthDateModal Flaga określająca, czy modal wyboru daty jest widoczny.
 * @property availableDoctors Lista dostępnych lekarzy do wyboru.
 * @property doctor Wybrany lekarz prowadzący.
 * @property doctorError Komunikat błędu dotyczący wyboru lekarza (jeśli występuje).
 * @property showDoctorDropdown Flaga określająca, czy rozwijane menu lekarzy jest widoczne.
 * @property dailyMeasurementRemindersCount Liczba powiadomień/pomiarów dziennie.
 * @property dailyMeasurementRemindersCountError Komunikat błędu dotyczący liczby powiadomień (jeśli występuje).
 * @property showDailyMeasurementCountDropdown Flaga określająca, czy rozwijane menu liczby pomiarów jest widoczne.
 * @property dailyMeasurementReminders Lista godzin przypomnień/pomiarów (mogą być puste).
 * @property dailyMeasurementRemindersErrors Lista komunikatów błędów dla poszczególnych godzin przypomnień.
 * @property showDailyMeasurementRemindersTimeModals Lista flag określających, które modale do wyboru godzin są widoczne.
 * @property loading Flaga określająca, czy trwa operacja ładowania (np. wysyłanie danych).
 */
data class SignUpScreenState(
    val step: SignUpScreenStep = SignUpScreenStep.CREDENTIALS,
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