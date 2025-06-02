package com.example.cardiotrack.services.auth

import com.example.cardiotrack.domain.User

sealed class AuthError : Throwable() {
    data object UserAlreadyExists : AuthError()
    data object UserNotFound : AuthError()
    data object Unexpected : AuthError()
}

interface AuthService {
    suspend fun user(): User?
    suspend fun signIn(email: String, password: String): User
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): User
}