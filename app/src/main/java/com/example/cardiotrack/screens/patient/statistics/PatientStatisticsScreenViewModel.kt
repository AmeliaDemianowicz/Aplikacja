package com.example.cardiotrack.screens.patient.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/**
 * ViewModel odpowiedzialny za logikę ekranu statystyk pacjenta.
 *
 * @property routeData Obiekt zawierający dane pacjenta przekazane przez nawigację.
 * @property patientService Serwis służący do pobierania pomiarów pacjenta z bazy danych.
 */
class PatientStatisticsScreenViewModel(
    private val routeData: PatientStatisticsScreen,
    private val patientService: PatientService
) : ViewModel() {
    /**
     * Reprezentuje bieżący stan ekranu statystyk.
     * Zawiera listę pomiarów i flagę ładowania.
     */
    val state = MutableStateFlow(PatientStatisticsScreenState())
    /**
     * Inicjalizator uruchamiany po utworzeniu ViewModelu.
     * Automatycznie pobiera dane pomiarowe pacjenta.
     */
    init {
        loadMeasurements()
    }
    /**
     * Pobiera pomiary pacjenta z serwisu i aktualizuje stan ekranu.
     * Operacja wykonywana jest asynchronicznie w coroutine.
     */
    fun loadMeasurements() {
        viewModelScope.launch {
            val measurements = patientService.getMeasurements(routeData.user)
            state.update { it.copy(measurements = measurements) }
        }
    }
}