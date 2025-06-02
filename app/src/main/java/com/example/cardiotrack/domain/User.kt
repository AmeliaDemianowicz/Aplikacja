package com.example.cardiotrack.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
sealed class User {
    @Serializable
    data class Doctor(val id: String) : User()

    @Serializable
    data class Patient(
        val id: String,
        val firstName: String,
        val lastName: String,
        // TODO: change to non-null
        val birthDate: LocalDate?,
        val sex: Sex?,
    ) : User()
}

@Serializable
enum class Sex {
    MAN,
    WOMAN
}