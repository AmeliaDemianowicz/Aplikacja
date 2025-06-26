package com.example.cardiotrack.screens.patient.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.cardiotrack.R
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.patient.FirebasePatientService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
/**
 * Dane przekazywane do ekranu dashboardu pacjenta.
 *
 * @property user Obiekt pacjenta, którego dane są wyświetlane na ekranie.
 */
@Serializable
data class PatientDashboardScreen(val user: User.Patient)
/**
 * Komponent wyświetlający dashboard pacjenta z widokiem kalendarza i listą pomiarów z wybranego dnia.
 *
 * Funkcjonalności:
 * - Nawigacja po miesiącach (poprzedni/następny miesiąc).
 * - Widok dni miesiąca z oznaczeniem dni zawierających pomiary.
 * - Wyświetlanie szczegółów pomiarów dla wybranego dnia.
 * - Usuwanie pomiarów przez gest przesunięcia (swipe).
 * - Dodawanie nowego pomiaru.
 * - Przejście do ekranu statystyk.
 *
 * @param routeData Dane ekranu zawierające pacjenta.
 * @param viewModel ViewModel zarządzający stanem i logiką ekranu.
 */

@Composable
fun PatientDashboardScreen(
    routeData: PatientDashboardScreen,
    viewModel: PatientDashboardScreenViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val daysInMonth = state.selectedMonth.lengthOfMonth()
    val firstDayOfWeek = (state.selectedMonth.atDay(1).dayOfWeek.value - 1)
    val currentDayMeasurements =
        state.measurements.filter { it.data.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.toJavaLocalDate() == state.selectedDate }
            .sortedBy { it.data.date }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo aplikacji",
                modifier = Modifier
                    .size(40.dp) // mały rozmiar
                    .padding(start = 4.dp)
            )
            IconButton(onClick = viewModel::handleGoToPrevMonth) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }

            Text(
                text = "${
                    state.selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                } ${state.selectedMonth.year}", style = MaterialTheme.typography.titleLarge)

            IconButton(onClick = viewModel::handleGoToNextMonth) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = false
        ) {
            items(firstDayOfWeek) {
                Box(modifier = Modifier.height(72.dp))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val date = state.selectedMonth.atDay(day)
                val measurementsForDay = state.measurements.filter {
                    it.data.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.toJavaLocalDate() == date
                }
                val hasEvent = measurementsForDay.isNotEmpty()
                val isSelected = date == state.selectedDate
                val isInRange = viewModel.isDayInReferenceRange(date)
                val isEnabled = date <= LocalDate.now()

                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .height(72.dp)
                        .clickable(
                            enabled = isEnabled,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            viewModel.handleSelectedDateChange(date)
                        }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = if (!isEnabled) Color.Transparent
                                    else if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent, shape = CircleShape
                                ), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (!isEnabled) MaterialTheme.colorScheme.surfaceDim
                                else if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else Color.Unspecified,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        if (hasEvent) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (isInRange) Color.Green.copy(alpha = 0.5f)
                                            else Color.Red.copy(alpha = 0.5f), shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = measurementsForDay.size.toString(),
                                    modifier = Modifier.padding(4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(0.75f)) {
            items(currentDayMeasurements, key = { it.id }) { measurement ->
                val swipeState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteMeasurement(measurement)
                        }
                        it != SwipeToDismissBoxValue.Settled
                    }
                )

                OutlinedCard(modifier = Modifier.padding(8.dp)) {
                    SwipeToDismissBox(
                        state = swipeState,
                        modifier = Modifier.animateContentSize(),
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Usuń pomiar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red.copy(0.3f))
                                        .wrapContentSize(Alignment.CenterEnd)
                                        .padding(12.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                measurement.data.date.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .toJavaLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontWeight = FontWeight.Bold
                            )
                            Text("SYS: ${measurement.data.sys} mmHg")
                            Text("DIA: ${measurement.data.dia} mmHg")
                            Text("HR: ${measurement.data.bpm} bpm")
                            if (measurement.data.notes != null) {
                                Text("Notatki:")
                                Text(measurement.data.notes)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = viewModel::addMeasurement,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Text("Dodaj pomiar")
        }

        TextButton(
            onClick = viewModel::redirectToStatistics,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Text("Statystyki")
        }
    }
}
/**
 * Podgląd composable `PatientDashboardScreen` w trybie podglądu Android Studio.
 *
 * Tworzy przykładowego użytkownika pacjenta i inicjalizuje ekran
 */
@Preview
@Composable
fun PatientDashboardScreenPreview() {
    val navController = rememberNavController()
    val routeData = PatientDashboardScreen(
        User.Patient(
            id = "8hVttJkwXdQpFTZIFEwWso6pHv13",
            doctorId = "",
            firstName = "",
            lastName = "",
            birthDate = Clock.System.now(),
            sex = Sex.MAN,
            pesel = "12345678901"
        )
    )

    PatientDashboardScreen(
        routeData = routeData, viewModel = viewModel(factory = viewModelFactory {
            initializer {
                PatientDashboardScreenViewModel(
                    routeData, FirebasePatientService(), navController
                )
            }
        })
    )
}
