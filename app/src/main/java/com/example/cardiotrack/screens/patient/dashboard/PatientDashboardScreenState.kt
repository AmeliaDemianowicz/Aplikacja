package com.example.cardiotrack.screens.patient.dashboard

import com.example.cardiotrack.domain.Measurement
import java.time.LocalDate
import java.time.YearMonth
/**
 * Stan ekranu pulpitu pacjenta.
 *
 * @property selectedMonth Aktualnie wybrany miesiąc, domyślnie bieżący miesiąc.
 * @property selectedDate Aktualnie wybrany dzień, domyślnie dzisiejsza data.
 * @property measurements Lista pomiarów związanych z pacjentem.
 */
data class PatientDashboardScreenState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val measurements: List<Measurement> = emptyList()
)