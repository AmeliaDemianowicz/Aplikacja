package com.example.cardiotrack.screens.patient.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cardiotrack.R
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.patient.FirebasePatientService
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable


@Serializable
data class PatientStatisticsScreen(val user: User.Patient)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientStatisticsScreen(viewModel: PatientStatisticsScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val weeklyAverage = state.weeklyAverage()
    val monthlyAverage = state.monthlyAverage()
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo aplikacji",
                    modifier = Modifier
                        .size(40.dp) // mały rozmiar
                        .padding(start = 4.dp)
                )
            }
        }
    )

    if (state.loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 144.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            // Średnia z 7 dni
            Text(
                text = "Średnia wartość z 7 dni",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (weeklyAverage != null) {
                Text("SYS: ${weeklyAverage.sys} mmHg")
                Text("DIA: ${weeklyAverage.dia} mmHg")
                Text("HR: ${weeklyAverage.bpm} bpm")
            } else {
                Text("Brak danych")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Średnia z 30 dni
            Text(
                text = "Średnia wartość z 30 dni",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (monthlyAverage != null) {
                Text("SYS: ${monthlyAverage.sys} mmHg")
                Text("DIA: ${monthlyAverage.dia} mmHg")
                Text("HR: ${monthlyAverage.bpm} bpm")
            } else {
                Text("Brak danych")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dni w normie
            Text(
                text = "\u2705 Liczba pomiarów w normie",
                fontWeight = FontWeight.Bold
            )
            Text("${state.daysInReferenceRange()}")

            Spacer(modifier = Modifier.height(8.dp))

            // Dni poza normą
            Text(
                text = "\u274C Liczba pomiarów poza normą",
                fontWeight = FontWeight.Bold
            )
            Text("${state.daysNotInReferenceRange()}")

            Spacer(modifier = Modifier.height(16.dp))

            // Zakresy referencyjne
            Text(
                text = "*Zakresy referencyjne",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text("SYS: 90–129 mmHg")
            Text("DIA: 60–84 mmHg")
            Text("HR: 60–100 uderzeń/min (w spoczynku)")
        }
    }
}


@Preview
@Composable
fun PatientStatisticsScreenPreview() {
    val routeData = PatientStatisticsScreen(
        User.Patient(
            id = "TEST",
            doctorId = "",
            firstName = "",
            lastName = "",
            birthDate = Clock.System.now(),
            sex = Sex.MAN,
            pesel = "12345678901"
        )
    )

    PatientStatisticsScreen(
        viewModel = viewModel(factory = viewModelFactory {
            initializer { PatientStatisticsScreenViewModel(routeData, FirebasePatientService()) }
        })
    )
}