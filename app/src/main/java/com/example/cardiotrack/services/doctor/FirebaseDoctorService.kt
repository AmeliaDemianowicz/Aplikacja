package com.example.cardiotrack.services.doctor

import com.example.cardiotrack.database.FirebaseUser
import com.example.cardiotrack.database.FirebaseUserType
import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
/**
 * Implementacja interfejsu [DoctorService] wykorzystująca Firebase Firestore.
 * Umożliwia lekarzowi pobieranie listy przypisanych pacjentów z opcją filtrowania i paginacji.
 */
class FirebaseDoctorService : DoctorService {
    /**
     * Pobiera listę pacjentów przypisanych do danego lekarza, opcjonalnie filtrując po imieniu i nazwisku.
     * Wyniki są ograniczane do określonego limitu i mogą być stronicowane przy użyciu kursora.
     *
     * @param doctor Obiekt lekarza, którego pacjenci mają zostać pobrani.
     * @param fullName Tekst do filtrowania pacjentów według pełnego imienia i nazwiska.
     * @param cursor Dokument z poprzedniego zapytania służący jako punkt startowy do kolejnej strony danych.
     * @param limit Maksymalna liczba pacjentów do pobrania.
     *
     * @return [PatientsPage] zawierająca listę pacjentów, ostatni dokument (kursor) i informację czy są kolejne wyniki.
     */
    override suspend fun getPatientsByName(
        doctor: User.Doctor,
        fullName: String,
        cursor: DocumentSnapshot?,
        limit: Long
    ): PatientsPage {
        var query = Firebase.firestore.collection("users")
            .whereEqualTo("type", FirebaseUserType.PATIENT)
            .whereEqualTo("doctorId", doctor.id)
            .whereGreaterThanOrEqualTo("fullName", fullName)
            .whereLessThan("fullName", fullName + "\uf8ff")
            .orderBy("fullName")
            .limit(limit)

        if (cursor != null) {
            query = query.startAfter(cursor)
        }

        val snapshot = query.get().await()

        return PatientsPage(
            data = snapshot.toObjects<FirebaseUser>().map { FirebaseUser.deserializePatient(it) },
            cursor = snapshot.documents.lastOrNull(),
            hasMore = snapshot.documents.size >= limit
        )
    }
}