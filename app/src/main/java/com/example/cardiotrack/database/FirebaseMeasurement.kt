package com.example.cardiotrack.database

data class FirebaseMeasurement(
    val userId: String? = null,
    val bpm: Int? = null,
    val sys: Int? = null,
    val dia: Int? = null,
    val date: Long? = null,
    val notes: String? = null,
)