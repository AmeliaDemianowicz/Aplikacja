package com.example.cardiotrack.services.doctor

import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.DocumentSnapshot

data class PatientsPage(
    val data: List<User.Patient>,
    val cursor: DocumentSnapshot?,
    val hasMore: Boolean
)

interface DoctorService {
    suspend fun getPatientsByName(
        doctor: User.Doctor,
        fullName: String = "",
        cursor: DocumentSnapshot? = null,
        limit: Long = 20
    ): PatientsPage
}