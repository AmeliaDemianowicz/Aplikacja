package com.example.cardiotrack.services.doctor

import com.example.cardiotrack.database.FirebaseUser
import com.example.cardiotrack.database.FirebaseUserType
import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseDoctorService : DoctorService {
    override suspend fun getPatientsByName(
        fullName: String,
        offset: Int,
        limit: Int
    ): List<User.Patient> {
        return Firebase.firestore.collection("users")
            .whereEqualTo("type", FirebaseUserType.PATIENT)
            .whereGreaterThanOrEqualTo("fullName", fullName)
            .orderBy("fullName")
            .startAt(offset)
            .limit(limit.toLong())
            .get()
            .await()
            .map { it.toObject<FirebaseUser>() }
            .map { FirebaseUser.deserializePatient(it) }
    }
}