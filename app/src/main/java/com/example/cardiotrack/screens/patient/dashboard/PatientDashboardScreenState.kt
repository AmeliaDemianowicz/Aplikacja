package com.example.cardiotrack.screens.patient.dashboard

import com.example.cardiotrack.domain.Measurement
import java.time.LocalDate
import java.time.YearMonth

data class PatientDashboardScreenState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val measurements: List<Measurement> = emptyList()
)