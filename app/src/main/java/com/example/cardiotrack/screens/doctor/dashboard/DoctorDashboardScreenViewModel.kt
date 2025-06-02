package com.example.cardiotrack.screens.doctor.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardiotrack.services.doctor.DoctorService
import kotlinx.coroutines.launch

class DoctorDashboardScreenViewModel(private val doctorService: DoctorService) : ViewModel() {
    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val patients = doctorService.getPatientsByName()
            println(patients)
        }
    }
}