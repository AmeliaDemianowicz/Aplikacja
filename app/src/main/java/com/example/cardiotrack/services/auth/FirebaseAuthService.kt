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

class FirebaseAuthService : AuthService {
    private val users = Firebase.firestore.collection("users")

    override suspend fun user(): User? {
        val userId = Firebase.auth.currentUser?.uid
        return userId?.let { getUserById(it) }
    }

    override suspend fun getAvailableDoctors(): List<User.Doctor> {
        return users.whereEqualTo("type", FirebaseUserType.DOCTOR)
            .get().await()
            .toObjects<FirebaseUser>()
            .map { FirebaseUser.deserializeDoctor(it) }
    }

    override suspend fun signIn(email: String, password: String): User {
        try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw AuthError.Unexpected
            return getUserById(userId)
        } catch (_: FirebaseAuthInvalidUserException) {
            throw AuthError.UserNotFound
        }
    }

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
        try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw AuthError.Unexpected
            return User.Patient(userId, doctor.id, firstName, lastName, birthDate, sex, pesel)
                .also { setUserById(userId, it) }
        } catch (_: FirebaseAuthUserCollisionException) {
            throw AuthError.UserAlreadyExists
        }
    }

    private suspend fun getUserById(userId: String): User {
        val userData = users.document(userId).get().await().toObject<FirebaseUser>()
        return userData?.let { FirebaseUser.deserialize(it) } ?: throw AuthError.Unexpected
    }

    private suspend fun setUserById(userId: String, user: User) {
        users.document(userId).set(FirebaseUser.serialize(user)).await()
    }
}