package com.example.cardiotrack.services.auth

import com.example.cardiotrack.domain.User

interface DoctorService {
    suspend fun getPatients(page: Int): List<User.Patient>
}