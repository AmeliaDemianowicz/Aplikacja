package com.example.cardiotrack.domain

import kotlinx.datetime.Instant

data class Measurement(
    val bpm: Int,
    val sys: Int,
    val dia: Int,
    val date: Instant,
    val notes: String? = null
)