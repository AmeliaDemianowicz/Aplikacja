package com.example.cardiotrack.services.patient

import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User

interface PatientService {
    suspend fun getMeasurements(user: User.Patient): List<Measurement>
    suspend fun addMeasurement(user: User.Patient, data: MeasurementData)
    suspend fun deleteMeasurement(measurement: Measurement)
}