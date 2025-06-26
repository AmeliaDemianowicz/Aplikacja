package com.example.cardiotrack.services.auth

import com.example.cardiotrack.database.FirebaseUser
import com.example.cardiotrack.database.FirebaseUserType
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Instant
/**
 * Implementacja [AuthService] oparta na Firebase Authentication i Firestore.
 */
class FirebaseAuthService : AuthService {
    private val users = Firebase.firestore.collection("users")
    /**
     * Zwraca aktualnie zalogowanego użytkownika na podstawie identyfikatora Firebase UID.
     *
     * @return Obiekt [User] lub `null` jeśli użytkownik nie jest zalogowany.
     */
    override suspend fun user(): User? {
        val userId = Firebase.auth.currentUser?.uid
        return userId?.let { getUserById(it) }
    }
    /**
     * Pobiera listę dostępnych lekarzy z kolekcji `users` w Firestore.
     *
     * @return Lista obiektów typu [User.Doctor].
     */
    override suspend fun getAvailableDoctors(): List<User.Doctor> {
        return users.whereEqualTo("type", FirebaseUserType.DOCTOR)
            .get().await()
            .toObjects<FirebaseUser>()
            .map { FirebaseUser.deserializeDoctor(it) }
    }
    /**
     * Loguje użytkownika za pomocą adresu e-mail i hasła.
     *
     * @param email Adres e-mail użytkownika.
     * @param password Hasło użytkownika.
     * @return Obiekt [User] po pomyślnym zalogowaniu.
     * @throws AuthError.UserNotFound jeśli użytkownik nie istnieje.
     * @throws AuthError.Unexpected jeśli wystąpił nieoczekiwany błąd.
     */
    override suspend fun signIn(email: String, password: String): User {
        try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw AuthError.Unexpected
            return getUserById(userId)
        } catch (_: FirebaseAuthInvalidUserException) {
            throw AuthError.UserNotFound
        }
    }
    /**
     * Rejestruje nowego pacjenta i zapisuje dane do Firestore.
     *
     * @param doctor Lekarz przypisany do pacjenta.
     * @param email Adres e-mail.
     * @param password Hasło.
     * @param firstName Imię.
     * @param lastName Nazwisko.
     * @param birthDate Data urodzenia.
     * @param sex Płeć.
     * @param pesel Numer PESEL.
     * @return Obiekt [User.Patient] po pomyślnej rejestracji.
     * @throws AuthError.UserAlreadyExists jeśli e-mail jest już zarejestrowany.
     * @throws AuthError.PeselAlreadyExists jeśli PESEL już istnieje.
     * @throws AuthError.Unexpected jeśli wystąpił nieoczekiwany błąd.
     */
    override suspend fun signUp(
        doctor: User.Doctor,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Instant,
        sex: Sex,
        pesel: String
    ): User {
        if (!isPeselAvailable(pesel)) {
            throw AuthError.PeselAlreadyExists
        }
        try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw AuthError.Unexpected
            return User.Patient(userId, doctor.id, firstName, lastName, birthDate, sex, pesel)
                .also { setUserById(userId, it) }
        } catch (_: FirebaseAuthUserCollisionException) {
            throw AuthError.UserAlreadyExists
        }
    }
    /**
     * Pobiera użytkownika z Firestore na podstawie jego UID.
     *
     * @param userId Identyfikator użytkownika Firebase.
     * @return Obiekt [User].
     * @throws AuthError.Unexpected jeśli dane użytkownika nie zostały znalezione lub są niepoprawne.
     */
    private suspend fun getUserById(userId: String): User {
        val userData = users.document(userId).get().await().toObject<FirebaseUser>()
        return userData?.let { FirebaseUser.deserialize(it) } ?: throw AuthError.Unexpected
    }
    /**
     * Zapisuje dane użytkownika do Firestore.
     *
     * @param userId Identyfikator użytkownika.
     * @param user Obiekt [User], który ma zostać zapisany.
     */
    private suspend fun setUserById(userId: String, user: User) {
        users.document(userId).set(FirebaseUser.serialize(user)).await()
    }
    /**
     * Sprawdza, czy dany numer PESEL jest dostępny (czy nie został jeszcze użyty).
     *
     * @param pesel Numer PESEL do sprawdzenia.
     * @return `true` jeśli PESEL jest dostępny, `false` jeśli już istnieje.
     */
    private suspend fun isPeselAvailable(pesel: String): Boolean {
        return users
            .whereEqualTo("type", FirebaseUserType.PATIENT)
            .whereEqualTo("pesel", pesel)
            .limit(1).get().await()
            .isEmpty
    }
}