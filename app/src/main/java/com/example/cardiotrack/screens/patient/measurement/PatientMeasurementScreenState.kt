package com.example.cardiotrack.screens.patient.measurement

import kotlinx.datetime.Instant

data class PatientMeasurementScreenState(
    val bpm: String = "",
    val bpmError: String? = null,
    val sys: String = "",
    val sysError: String? = null,
    val dia: String = "",
    val diaError: String? = null,
    val date: Instant? = null,
    val dateError: String? = null,
    val showTimeModal: Boolean = false,
    val notes: String? = null,
    val loading: Boolean = false
)