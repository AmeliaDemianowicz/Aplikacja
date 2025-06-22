package com.example.cardiotrack.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class User {
    @Serializable
    data class Doctor(
        val id: String,
        val firstName: String,
        val lastName: String
    ) : User()

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

@Serializable
enum class Sex {
    MAN,
    WOMAN
}