package com.example.cardiotrack.screens.patient.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.screens.patient.measurement.PatientMeasurementScreen
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreen
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

class PatientDashboardScreenViewModel(
    private val routeData: PatientDashboardScreen,
    private val patientService: PatientService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(PatientDashboardScreenState())

    init {
        loadMeasurements()
    }

    fun handleGoToPrevMonth() {
        state.update {
            val selectedMonth = it.selectedMonth.minusMonths(1)
            val selectedDate =
                it.selectedDate
                    .withYear(selectedMonth.year)
                    .withMonth(selectedMonth.monthValue)
                    .withDayOfMonth(1)

            it.copy(selectedMonth = selectedMonth, selectedDate = selectedDate)
        }
    }

    fun handleGoToNextMonth() {
        state.update {
            val selectedMonth = it.selectedMonth.plusMonths(1)
            val selectedDate =
                it.selectedDate
                    .withYear(selectedMonth.year)
                    .withMonth(selectedMonth.monthValue)
                    .withDayOfMonth(1)

            it.copy(selectedMonth = selectedMonth, selectedDate = selectedDate)
        }
    }

    fun handleSelectedDateChange(date: LocalDate) {
        state.update { it.copy(selectedDate = date) }
    }

    fun addMeasurement() {
        navController.navigate(
            PatientMeasurementScreen(
                routeData.user,
                state.value.selectedDate.toKotlinLocalDate().toString()
            )
        )
    }

    fun loadMeasurements() {
        viewModelScope.launch {
            val measurements = patientService.getMeasurements(routeData.user)
            state.update { it.copy(measurements = measurements) }
        }
    }

    fun redirectToStatistics() {
        navController.navigate(PatientStatisticsScreen(routeData.user))
    }
}