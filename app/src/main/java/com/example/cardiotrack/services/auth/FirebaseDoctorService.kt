package com.example.cardiotrack.services.auth

import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseDoctorService : DoctorService {
    override suspend fun getPatients(page: Int): List<User.Patient> {
        val data = Firebase.firestore.collection("users").get().await()
        data.forEach { x -> x.data.forEach { k, v -> println("key: $k, value: $v")} }
        return emptyList()
    }
}