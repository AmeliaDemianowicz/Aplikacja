package com.example.cardiotrack.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.auth.signin.SignInScreen
import com.example.cardiotrack.screens.doctor.dashboard.DoctorDashboardScreen
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.services.auth.AuthService
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val authService: AuthService,
    private val navController: NavController
) : ViewModel() {
    init {
        redirectToNextScreen()
    }

    private fun redirectToNextScreen() {
        viewModelScope.launch {
            val user = authService.user()
            val nextScreen = when (user) {
                is User.Doctor -> DoctorDashboardScreen(user)
                is User.Patient -> PatientDashboardScreen(user)
                null -> SignInScreen
            }

            navController.navigate(nextScreen) {
                launchSingleTop = true
                popUpTo(SplashScreen) { inclusive = true }
            }
        }
    }
}