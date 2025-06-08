package com.example.cardiotrack.screens.patient.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.patient.FirebasePatientService
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class PatientStatisticsScreen(val user: User.Patient)

@Composable
fun PatientStatisticsScreen(viewModel: PatientStatisticsScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val weeklyAverage = state.weeklyAverage()
    val monthlyAverage = state.monthlyAverage()

    if (state.loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        Column {
            Text("Średnia wartość z 7 dni")
            if (weeklyAverage != null) {
                Text("SYS: ${weeklyAverage.sys} mmHg")
                Text("DIA: ${weeklyAverage.sys} mmHg")
                Text("HR: ${weeklyAverage.sys} bpm")
            } else {
                Text("Brak danych")
            }
            Text("Średnia wartość z 30 dni")
            if (monthlyAverage != null) {
                Text("SYS: ${monthlyAverage.sys} mmHg")
                Text("DIA: ${monthlyAverage.sys} mmHg")
                Text("HR: ${monthlyAverage.sys} bpm")
            } else {
                Text("Brak danych")
            }
            Text("Liczba dni w normie*")
            Text("${state.daysInReferenceRange()}")
            Text("Liczba dni poza normą*")
            Text("${state.daysNotInReferenceRange()}")

            Text("*Zakresy referencyjne")
            Text("SYS: 90-129 mmHg")
            Text("SYS: 60-84 mmHg")
            Text("SYS: 60-100 uderzeń na minute (w spoczynku)")
        }
    }
}


@Preview
@Composable
fun PatientStatisticsScreenPreview() {
    val routeData = PatientStatisticsScreen(
        User.Patient(
            id = "TEST",
            firstName = "",
            lastName = "",
            birthDate = Clock.System.now(),
            sex = Sex.MAN
        )
    )

    PatientStatisticsScreen(
        viewModel = viewModel(factory = viewModelFactory {
            initializer { PatientStatisticsScreenViewModel(routeData, FirebasePatientService()) }
        })
    )
}