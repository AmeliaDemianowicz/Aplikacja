package com.example.cardiotrack.screens.patient.dashboard

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.patient.measurement.PatientMeasurementScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class PatientDashboardScreenViewModel(private val navController: NavController) : ViewModel() {
    val state = MutableStateFlow(PatientDashboardScreenState())

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

    fun addMeasurement(user: User.Patient) {
        navController.navigate(PatientMeasurementScreen(user))
    }
}