package com.example.cardiotrack.screens.patient.measurement

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant

class PatientMeasurementScreenViewModel(
    private val patientService: PatientService,
    private val navController: NavController
) : ViewModel() {
    val state = MutableStateFlow(PatientMeasurementScreenState())

    fun handleBpmChange(bpm: String) {
        if (bpm.isDigitsOnly()) {
            state.update { it.copy(bpm = bpm, bpmError = null) }
        }
    }

    fun handleSysChange(sys: String) {
        if (sys.isDigitsOnly()) {
            state.update { it.copy(sys = sys, sysError = null) }
        }
    }

    fun handleDiaChange(dia: String) {
        if (dia.isDigitsOnly()) {
            state.update { it.copy(dia = dia, diaError = null) }
        }
    }

    fun handleTimeChange(date: LocalDate, hour: Int, minute: Int) {
        state.update {
            it.copy(
                date = date.atTime(hour, minute).toInstant(TimeZone.currentSystemDefault()),
                dateError = null
            )
        }
        hideTimeModal()
    }

    fun showTimeModal() {
        state.update { it.copy(showTimeModal = true) }
    }

    fun hideTimeModal() {
        state.update { it.copy(showTimeModal = false) }
    }

    fun handleNotesChange(notes: String) {
        state.update { it.copy(notes = notes) }
    }

    fun addMeasurement(user: User.Patient) {
        viewModelScope.launch {
            validateForm()
            val measurementData = state.value
            if (measurementData.date != null) {
                patientService.addMeasurement(
                    user,
                    MeasurementData(
                        bpm = measurementData.bpm.toInt(),
                        sys = measurementData.sys.toInt(),
                        dia = measurementData.dia.toInt(),
                        date = measurementData.date,
                        notes = measurementData.notes
                    )
                )
                navController.navigate(PatientDashboardScreen(user))
            }
        }
    }

    private fun validateForm() {
        validateBpmField()
        validateSysField()
        validateDiaField()
        validateDateField()
    }

    private fun validateBpmField() {
        if (state.value.bpm.isBlank()) {
            return state.update { it.copy(bpmError = "Puls nie może być pusty") }
        }
        if (state.value.bpm.toIntOrNull() == null) {
            return state.update { it.copy(bpmError = "Nieprawidłowy puls") }
        }
    }

    private fun validateSysField() {
        if (state.value.sys.isBlank()) {
            return state.update { it.copy(sysError = "Ciśnienie nie może być puste") }
        }
        if (state.value.sys.toIntOrNull() == null) {
            return state.update { it.copy(sysError = "Nieprawidłowe ciśnienie") }
        }
    }

    private fun validateDiaField() {
        if (state.value.dia.isBlank()) {
            return state.update { it.copy(diaError = "Ciśnienie nie może być puste") }
        }
        if (state.value.dia.toIntOrNull() == null) {
            return state.update { it.copy(diaError = "Nieprawidłowe ciśnienie") }
        }
    }

    private fun validateDateField() {
        if (state.value.date == null) {
            return state.update { it.copy(dateError = "Data nie może być pusta") }
        }
    }
}