package com.example.cardiotrack


import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.savedstate.SavedState
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.auth.signin.SignInScreen
import com.example.cardiotrack.screens.auth.signin.SignInScreenViewModel
import com.example.cardiotrack.screens.auth.signup.SignUpScreen
import com.example.cardiotrack.screens.auth.signup.SignUpScreenViewModel
import com.example.cardiotrack.screens.doctor.dashboard.DoctorDashboardScreen
import com.example.cardiotrack.screens.doctor.dashboard.DoctorDashboardScreenViewModel
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreenViewModel
import com.example.cardiotrack.screens.patient.measurement.PatientMeasurementScreen
import com.example.cardiotrack.screens.patient.measurement.PatientMeasurementScreenViewModel
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreen
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreenViewModel
import com.example.cardiotrack.services.auth.FirebaseAuthService
import com.example.cardiotrack.services.doctor.FirebaseDoctorService
import com.example.cardiotrack.services.patient.FirebasePatientService
import com.example.cardiotrack.ui.theme.CardioTrackTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf


@Composable
fun CardioTrackApp() {
    CardioTrackTheme {
        val navController = rememberNavController()
        val authService = remember { FirebaseAuthService() }
        val doctorService = remember { FirebaseDoctorService() }
        val patientService = remember { FirebasePatientService() }

        val typeMap = mapOf(
            jsonTypeMap<User.Doctor>(),
            jsonTypeMap<User.Patient>(),
        )

        NavHost(navController, startDestination = SignInScreen) {
            composable<SignInScreen>(typeMap) {
                SignInScreen(
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer { SignInScreenViewModel(authService, navController) }
                    })
                )
            }
            composable<SignUpScreen>(typeMap) {
                SignUpScreen(
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer { SignUpScreenViewModel(authService, navController) }
                    })
                )
            }
            composable<DoctorDashboardScreen>(typeMap) {
                DoctorDashboardScreen(
                    routeData = it.toRoute<DoctorDashboardScreen>(),
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer {
                            DoctorDashboardScreenViewModel(
                                it.toRoute<DoctorDashboardScreen>(),
                                doctorService,
                                navController
                            )
                        }
                    })
                )
            }
            composable<PatientDashboardScreen>(typeMap) {
                PatientDashboardScreen(
                    routeData = it.toRoute<PatientDashboardScreen>(),
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer {
                            PatientDashboardScreenViewModel(
                                it.toRoute<PatientDashboardScreen>(),
                                patientService,
                                navController
                            )
                        }
                    })
                )
            }
            composable<PatientMeasurementScreen>(typeMap) {
                PatientMeasurementScreen(
                    routeData = it.toRoute<PatientMeasurementScreen>(),
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer {
                            PatientMeasurementScreenViewModel(
                                patientService,
                                navController
                            )
                        }
                    })
                )
            }
            composable<PatientStatisticsScreen>(typeMap) {
                PatientStatisticsScreen(
                    viewModel = viewModel(factory = viewModelFactory {
                        initializer {
                            PatientStatisticsScreenViewModel(
                                it.toRoute<PatientStatisticsScreen>(),
                                patientService
                            )
                        }
                    })
                )
            }
        }
    }
}


inline fun <reified T> jsonTypeMap(): Pair<KType, NavType<T>> {
    return typeOf<T>() to (object : NavType<T>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): T? =
            bundle.getString(key)?.let { parseValue(it) }

        override fun put(bundle: SavedState, key: String, value: T) {
            bundle.putString(key, serializeAsValue(value))
        }

        override fun parseValue(value: String): T = Json.decodeFromString<T>(value)

        override fun serializeAsValue(value: T): String = Json.encodeToString(value)
    })
}


