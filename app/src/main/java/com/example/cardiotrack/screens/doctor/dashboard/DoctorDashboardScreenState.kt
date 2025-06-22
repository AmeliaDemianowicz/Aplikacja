package com.example.cardiotrack.screens.doctor.dashboard

import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.DocumentSnapshot

data class DoctorDashboardScreenState(
    val fullNameSearchTerm: String = "",
    val patients: List<User.Patient> = emptyList(),
    val cursor: DocumentSnapshot? = null,
    val loading: Boolean = false,
    val hasMore: Boolean = true
)