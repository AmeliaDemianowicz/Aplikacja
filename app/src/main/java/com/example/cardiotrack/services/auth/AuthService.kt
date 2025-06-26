package com.example.cardiotrack.services.auth

import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import kotlinx.datetime.Instant
/**
 * Reprezentuje błędy, które mogą wystąpić podczas operacji uwierzytelniania.
 */
sealed class AuthError : Throwable() {
    /** Występuje, gdy użytkownik z podanym adresem e-mail już istnieje. */
    data object UserAlreadyExists : AuthError()
    /** Występuje, gdy użytkownik z podanym numerem PESEL już istnieje. */
    data object PeselAlreadyExists : AuthError()
    /** Występuje, gdy użytkownik nie został znaleziony w systemie. */
    data object UserNotFound : AuthError()
    /** Niespodziewany błąd, który nie pasuje do żadnej z powyższych kategorii. */
    data object Unexpected : AuthError()
}
/**
 * Interfejs definiujący operacje związane z uwierzytelnianiem i rejestracją użytkowników.
 */
interface AuthService {
    /**
     * Zwraca aktualnie zalogowanego użytkownika, jeśli taki istnieje.
     *
     * @return [User] lub `null`, jeśli nikt nie jest zalogowany.
     */
    suspend fun user(): User?
    /**
     * Zwraca listę dostępnych lekarzy, do których pacjent może się przypisać podczas rejestracji.
     *
     * @return Lista obiektów typu [User.Doctor].
     */
    suspend fun getAvailableDoctors(): List<User.Doctor>
    /**
     * Loguje użytkownika przy użyciu e-maila i hasła.
     *
     * @param email Adres e-mail użytkownika.
     * @param password Hasło użytkownika.
     * @return Obiekt [User] po pomyślnym zalogowaniu.
     * @throws AuthError Gdy logowanie się nie powiedzie.
     */
    suspend fun signIn(email: String, password: String): User
    /**
     * Rejestruje nowego pacjenta w systemie.
     *
     * @param doctor Lekarz przypisany do pacjenta.
     * @param email Adres e-mail pacjenta.
     * @param password Hasło pacjenta.
     * @param firstName Imię pacjenta.
     * @param lastName Nazwisko pacjenta.
     * @param birthDate Data urodzenia pacjenta.
     * @param sex Płeć pacjenta.
     * @param pesel Numer PESEL pacjenta.
     * @return Obiekt [User] po pomyślnej rejestracji.
     * @throws AuthError Jeśli wystąpił problem podczas rejestracji.
     */
    suspend fun signUp(
        doctor: User.Doctor,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Instant,
        sex: Sex,
        pesel: String,
    ): User
}