package com.example.cardiotrack.screens.patient.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cardiotrack.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class PatientDashboardScreen(val user: User.Patient)

@Composable
fun PatientDashboardScreen(routeData: PatientDashboardScreen) {
    Text("Dashboard (Patient): ${routeData.user.id}")
}