package com.example.cardiotrack.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class User {
    @Serializable
    data class Doctor(val id: String) : User()

    @Serializable
    data class Patient(val id: String, val firstName: String, val lastName: String) : User()
}
