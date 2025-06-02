package com.example.cardiotrack.services.doctor

import com.example.cardiotrack.domain.User

interface DoctorService {
    suspend fun getPatientsByName(
        fullName: String = "",
        offset: Int = 0,
        limit: Int = 20
    ): List<User.Patient>
}