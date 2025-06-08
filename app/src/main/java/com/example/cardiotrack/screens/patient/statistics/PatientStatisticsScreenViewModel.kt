package com.example.cardiotrack.screens.patient.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientStatisticsScreenViewModel(
    private val routeData: PatientStatisticsScreen,
    private val patientService: PatientService
) : ViewModel() {
    val state = MutableStateFlow(PatientStatisticsScreenState())

    init {
        loadMeasurements()
    }

    fun loadMeasurements() {
        viewModelScope.launch {
            val measurements = patientService.getMeasurements(routeData.user)
            state.update { it.copy(measurements = measurements) }
        }
    }
}