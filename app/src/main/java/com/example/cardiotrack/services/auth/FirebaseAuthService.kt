package com.example.cardiotrack.services.auth

import com.example.cardiotrack.domain.User
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

enum class FirebaseUserType {
    DOCTOR,
    PATIENT
}

data class FirebaseUser(
    val type: FirebaseUserType? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

class FirebaseAuthService : AuthService {
    private val users = Firebase.firestore.collection("users")

    override suspend fun user(): User? {
        val userId = Firebase.auth.currentUser?.uid
        return userId?.let { getUserById(it) }
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
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): User {
        try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw AuthError.Unexpected
            return User.Patient(userId, firstName, lastName).also { setUserById(userId, it) }
        } catch (_: FirebaseAuthUserCollisionException) {
            throw AuthError.UserAlreadyExists
        }
    }

    private suspend fun getUserById(userId: String): User {
        val userData = users.document(userId).get().await().toObject<FirebaseUser>()
        return userData?.let { deserializeUser(userId, it) } ?: throw AuthError.Unexpected
    }

    private suspend fun setUserById(userId: String, user: User) {
        users.document(userId).set(serializeUser(user)).await()
    }

    private fun deserializeUser(userId: String, user: FirebaseUser): User {
        return when (user.type) {
            FirebaseUserType.DOCTOR -> User.Doctor(userId)
            FirebaseUserType.PATIENT -> deserializePatient(userId, user)
            null -> throw AuthError.Unexpected
        }
    }

    private fun deserializePatient(userId: String, user: FirebaseUser): User.Patient {
        if (user.firstName == null || user.lastName == null) {
            throw AuthError.Unexpected
        }

        return User.Patient(userId, firstName = user.firstName, lastName = user.lastName)
    }

    private fun serializeUser(user: User): FirebaseUser {
        return when (user) {
            is User.Doctor -> FirebaseUser(
                type = FirebaseUserType.DOCTOR,
            )

            is User.Patient -> FirebaseUser(
                type = FirebaseUserType.PATIENT,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }
    }

}