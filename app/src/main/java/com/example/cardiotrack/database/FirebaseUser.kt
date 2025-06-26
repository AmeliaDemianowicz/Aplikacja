package com.example.cardiotrack.database

import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import kotlinx.datetime.Instant
/**
 * Enum określający typ użytkownika Firebase.
 */
enum class FirebaseUserType {
    /** Użytkownik będący lekarzem. */
    DOCTOR,
    /** Użytkownik będący pacjentem. */
    PATIENT
}
/**
 * Reprezentacja użytkownika przechowywanego w bazie Firebase.
 *
 * @property id Identyfikator użytkownika.
 * @property doctorId Identyfikator przypisanego lekarza (dla pacjenta).
 * @property type Typ użytkownika (lekarz lub pacjent).
 * @property firstName Imię użytkownika.
 * @property lastName Nazwisko użytkownika.
 * @property fullName Pełne imię i nazwisko użytkownika.
 * @property birthDate Data urodzenia (dla pacjenta).
 * @property sex Płeć użytkownika.
 * @property pesel Numer PESEL użytkownika.
 */
data class FirebaseUser(
    val id: String? = null,
    val doctorId: String? = null,
    val type: FirebaseUserType? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val birthDate: Long? = null,
    val sex: Sex? = null,
    val pesel: String? = null
) {
    companion object {
        /**
         * Deserializuje obiekt [FirebaseUser] do odpowiedniego typu [User] na podstawie pola [type].
         *
         * @param user Użytkownik w formacie Firebase.
         * @return Obiekt domenowy [User].
         * @throws IllegalStateException jeśli typ użytkownika jest niezdefiniowany.
         */
        fun deserialize(user: FirebaseUser): User {
            checkNotNull(user.type)
            return when (user.type) {
                FirebaseUserType.DOCTOR -> deserializeDoctor(user)
                FirebaseUserType.PATIENT -> deserializePatient(user)
            }
        }
        /**
         * Serializuje obiekt domenowy [User] do formatu [FirebaseUser].
         *
         * @param user Użytkownik domenowy.
         * @return Obiekt gotowy do zapisania w Firebase.
         */

        fun serialize(user: User): FirebaseUser {
            return when (user) {
                is User.Doctor -> serializeDoctor(user)
                is User.Patient -> serializePatient(user)
            }
        }
        /**
         * Deserializuje obiekt [FirebaseUser] do typu [User.Doctor].
         *
         * @param user Użytkownik w formacie Firebase (lekarz).
         * @return Obiekt domenowy lekarza.
         * @throws IllegalStateException jeśli którekolwiek z wymaganych pól jest puste.
         */
        fun deserializeDoctor(user: FirebaseUser): User.Doctor {
            checkNotNull(user.id)
            checkNotNull(user.firstName)
            checkNotNull(user.lastName)
            return User.Doctor(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }
        /**
         * Deserializuje obiekt [FirebaseUser] do typu [User.Patient].
         *
         * @param user Użytkownik w formacie Firebase (pacjent).
         * @return Obiekt domenowy pacjenta.
         * @throws IllegalStateException jeśli którekolwiek z wymaganych pól jest puste.
         */
        fun deserializePatient(user: FirebaseUser): User.Patient {
            checkNotNull(user.id)
            checkNotNull(user.doctorId)
            checkNotNull(user.firstName)
            checkNotNull(user.lastName)
            checkNotNull(user.birthDate)
            checkNotNull(user.sex)
            checkNotNull(user.pesel)
            return User.Patient(
                id = user.id,
                doctorId = user.doctorId,
                firstName = user.firstName,
                lastName = user.lastName,
                birthDate = Instant.fromEpochMilliseconds(user.birthDate),
                sex = user.sex,
                pesel = user.pesel,
            )
        }
        /**
         * Serializuje obiekt [User.Doctor] do formatu [FirebaseUser].
         *
         * @param user Obiekt lekarza.
         * @return Obiekt użytkownika gotowy do zapisania w Firebase.
         */
        fun serializeDoctor(user: User.Doctor): FirebaseUser {
            return FirebaseUser(
                id = user.id,
                type = FirebaseUserType.DOCTOR,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }
        /**
         * Serializuje obiekt [User.Patient] do formatu [FirebaseUser].
         *
         * @param user Obiekt pacjenta.
         * @return Obiekt użytkownika gotowy do zapisania w Firebase.
         */
        fun serializePatient(user: User.Patient): FirebaseUser {
            return FirebaseUser(
                id = user.id,
                type = FirebaseUserType.PATIENT,
                doctorId = user.doctorId,
                firstName = user.firstName,
                lastName = user.lastName,
                fullName = "${user.firstName} ${user.lastName}",
                birthDate = user.birthDate.toEpochMilliseconds(),
                sex = user.sex,
                pesel = user.pesel
            )
        }
    }
}