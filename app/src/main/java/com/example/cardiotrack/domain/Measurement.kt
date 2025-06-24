package com.example.cardiotrack.domain

import kotlinx.datetime.Instant

data class Measurement(
    val id: String,
    val data: MeasurementData
)

data class MeasurementData(
    val bpm: Int,
    val sys: Int,
    val dia: Int,
    val date: Instant,
    val notes: String? = null
)