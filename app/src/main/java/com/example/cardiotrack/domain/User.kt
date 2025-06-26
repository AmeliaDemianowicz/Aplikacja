package com.example.cardiotrack.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
/**
 * Reprezentuje użytkownika systemu – lekarza lub pacjenta.
 * Jest to klasa sealed, umożliwiająca rozróżnienie typów użytkowników.
 */
@Serializable
sealed class User {
    /**
     * Reprezentuje lekarza w systemie.
     *
     * @property id Unikalny identyfikator lekarza.
     * @property firstName Imię lekarza.
     * @property lastName Nazwisko lekarza.
     */
    @Serializable
    data class Doctor(
        val id: String,
        val firstName: String,
        val lastName: String
    ) : User()
    /**
     * Reprezentuje pacjenta w systemie.
     *
     * @property id Unikalny identyfikator pacjenta.
     * @property doctorId Identyfikator przypisanego lekarza.
     * @property firstName Imię pacjenta.
     * @property lastName Nazwisko pacjenta.
     * @property birthDate Data urodzenia pacjenta.
     * @property sex Płeć pacjenta.
     * @property pesel Numer PESEL pacjenta.
     */
    @Serializable
    data class Patient(
        val id: String,
        val doctorId: String,
        val firstName: String,
        val lastName: String,
        val birthDate: Instant,
        val sex: Sex,
        val pesel: String,
    ) : User()
}
/**
 * Enum określający płeć użytkownika.
 */
@Serializable
enum class Sex {
    /** Mężczyzna. */
    MAN,
    /** Kobieta. */
    WOMAN
}