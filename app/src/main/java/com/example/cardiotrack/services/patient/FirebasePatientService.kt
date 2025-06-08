package com.example.cardiotrack.services.patient

import com.example.cardiotrack.database.FirebaseMeasurement
import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebasePatientService : PatientService {
    override suspend fun addMeasurement(user: User.Patient, measurement: Measurement) {
        Firebase.firestore.collection("measurements")
            .add(
                FirebaseMeasurement(
                    userId = user.id,
                    bpm = measurement.bpm,
                    sys = measurement.sys,
                    dia = measurement.dia,
                    date = measurement.date.toEpochMilliseconds(),
                    notes = measurement.notes
                )
            )
    }
}