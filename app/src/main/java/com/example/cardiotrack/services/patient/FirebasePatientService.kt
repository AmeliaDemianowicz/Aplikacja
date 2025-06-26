package com.example.cardiotrack.services.patient

import com.example.cardiotrack.database.FirebaseMeasurement
import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
/**
 * Implementacja interfejsu [PatientService], która zarządza pomiarami pacjentów
 * przechowywanymi w bazie danych Firebase Firestore.
 */
class FirebasePatientService : PatientService {
    private val measurements = Firebase.firestore.collection("measurements")
    /**
     * Pobiera listę wszystkich pomiarów przypisanych do danego pacjenta.
     *
     * @param user Pacjent, dla którego mają zostać pobrane pomiary.
     * @return Lista pomiarów przypisana do pacjenta.
     */
    override suspend fun getMeasurements(user: User.Patient): List<Measurement> {
        return measurements
            .whereEqualTo("userId", user.id)
            .get().await()
            .toObjects<FirebaseMeasurement>()
            .map { FirebaseMeasurement.deserialize(it) }
    }
    /**
     * Dodaje nowy pomiar dla danego pacjenta do bazy danych.
     *
     * @param user Pacjent, do którego przypisany jest pomiar.
     * @param data Dane pomiaru zawierające ciśnienie krwi, puls, datę i opcjonalne notatki.
     */
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
    /**
     * Usuwa istniejący pomiar z bazy danych.
     *
     * @param measurement Pomiar do usunięcia.
     */
    override suspend fun deleteMeasurement(measurement: Measurement) {
        measurements.document(measurement.id).delete().await()
    }
}