package com.example.cardiotrack.services.patient

import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.User

interface PatientService {
    suspend fun addMeasurement(user: User.Patient, measurement: Measurement)
    suspend fun getMeasurements(user: User.Patient): List<Measurement>
}