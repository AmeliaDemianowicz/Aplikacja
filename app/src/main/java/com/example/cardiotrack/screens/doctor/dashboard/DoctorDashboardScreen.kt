package com.example.cardiotrack.screens.doctor.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cardiotrack.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class DoctorDashboardScreen(val user: User.Doctor)

@Composable
fun DoctorDashboardScreen(
    routeData: DoctorDashboardScreen,
    viewModel: DoctorDashboardScreenViewModel
) {
    Text("Dashboard (Doctor): ${routeData.user.id}")
}