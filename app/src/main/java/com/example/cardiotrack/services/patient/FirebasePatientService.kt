package com.example.cardiotrack.services.patient

import com.example.cardiotrack.database.FirebaseMeasurement
import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebasePatientService : PatientService {
    private val measurements = Firebase.firestore.collection("measurements")

    override suspend fun getMeasurements(user: User.Patient): List<Measurement> {
        return measurements
            .whereEqualTo("userId", user.id)
            .get().await()
            .toObjects<FirebaseMeasurement>()
            .map { FirebaseMeasurement.deserialize(it) }
    }

    override suspend fun addMeasurement(user: User.Patient, data: MeasurementData) {
        val measurementId = measurements.document().id
        val measurementData = FirebaseMeasurement(
            id = measurementId,
            userId = user.id,
            bpm = data.bpm,
            sys = data.sys,
            dia = data.dia,
            date = data.date.toEpochMilliseconds(),
            notes = data.notes
        )

        measurements.document(measurementId).set(measurementData).await()
    }

    override suspend fun deleteMeasurement(measurement: Measurement) {
        measurements.document(measurement.id).delete().await()
    }
}