package com.example.cardiotrack.screens.doctor.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreen
import com.example.cardiotrack.services.doctor.DoctorService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PAGE_SIZE = 25

class DoctorDashboardScreenViewModel(
    private val routeData: DoctorDashboardScreen,
    private val doctorService: DoctorService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(DoctorDashboardScreenState())

    init {
        loadNextPatientsPage()
    }

    fun handleSearchTermChange(searchTerm: String) {
        state.update {
            it.copy(
                fullNameSearchTerm = searchTerm,
                patients = emptyList(),
                cursor = null,
                hasMore = true
            )
        }
        loadNextPatientsPage()
    }

    fun loadNextPatientsPage() {
        if (state.value.loading || !state.value.hasMore) {
            return
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, error -> handleError(error) }) {
            state.update { it.copy(loading = true) }

            val (patients, cursor, hasMore) = doctorService.getPatientsByName(
                doctor = routeData.user,
                fullName = state.value.fullNameSearchTerm,
                cursor = state.value.cursor,
                limit = PAGE_SIZE.toLong()
            )

            state.update {
                it.copy(
                    loading = false,
                    patients = it.patients + patients,
                    cursor = cursor,
                    hasMore = hasMore
                )
            }
        }
    }

    fun redirectToStatistics(user: User.Patient) {
        navController.navigate(PatientStatisticsScreen(user))
    }

    private fun handleError(error: Throwable) {
        state.update { it.copy(loading = false) }
        // TODO: show error toast
    }
}