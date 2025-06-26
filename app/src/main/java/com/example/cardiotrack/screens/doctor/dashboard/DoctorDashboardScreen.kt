package com.example.cardiotrack.screens.doctor.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.cardiotrack.R
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.doctor.FirebaseDoctorService
import kotlinx.serialization.Serializable
//import java.time.Instant
import kotlinx.datetime.Instant
import java.time.Instant as JavaInstant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Period
/**
 * Dane trasy ekranu dashboardu lekarza.
 *
 * @property user Zalogowany lekarz.
 */
@Serializable
data class DoctorDashboardScreen(val user: User.Doctor)
/**
 * Formatuje datę urodzenia wraz z godziną na czytelny format tekstowy.
 *
 * @param instant Data i czas w formacie [kotlinx.datetime.Instant].
 * @return Sformatowany ciąg znaków w formacie "yyyy-MM-dd o HH:mm".
 */
fun formatBirthDateWithTime(instant: kotlinx.datetime.Instant): String {
    val javaInstant = java.time.Instant.ofEpochSecond(instant.epochSeconds)
    val localDateTime = java.time.LocalDateTime.ofInstant(javaInstant, java.time.ZoneId.systemDefault())
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    return "${localDateTime.format(dateFormatter)} o ${localDateTime.format(timeFormatter)}"
}
/**
 * Oblicza wiek na podstawie daty urodzenia.
 *
 * @param instant Data urodzenia w formacie [kotlinx.datetime.Instant].
 * @return Wiek w pełnych latach.
 */
fun calculateAge(instant: kotlinx.datetime.Instant): Int {
    val javaInstant = java.time.Instant.ofEpochSecond(instant.epochSeconds)
    val birthDate = java.time.LocalDateTime.ofInstant(javaInstant, java.time.ZoneId.systemDefault()).toLocalDate()
    val today = java.time.LocalDate.now()
    return java.time.Period.between(birthDate, today).years
}

/**
 * Ekran dashboardu lekarza wyświetlający listę pacjentów oraz pole wyszukiwania.
 *
 * @param routeData Dane przekazane do ekranu, zawierające aktualnego lekarza.
 * @param viewModel Model widoku zarządzający stanem i logiką.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    routeData: DoctorDashboardScreen, viewModel: DoctorDashboardScreenViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect { layoutInfo ->
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            if (lastVisibleItem >= totalItems - PAGE_SIZE) {
                viewModel.loadNextPatientsPage()
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 16.dp)
    ) {
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

        OutlinedTextField(
            label = { Text("Wyszukaj pacjenta") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            value = state.fullNameSearchTerm,
            onValueChange = viewModel::handleSearchTermChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
        )

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(state.patients, key = { it.id }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.redirectToStatistics(it) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil pacjenta",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("${it.firstName} ${it.lastName}")
                        Text("Data ur.: ${formatBirthDateWithTime(it.birthDate)} (${calculateAge(it.birthDate)} lat), Płeć: ${it.sex}")
                        Text(text = "PESEL: ${it.pesel}")
                    }
                }
            }

            if (state.loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

/**
 * Podgląd ekranu dashboardu lekarza w trybie podglądu Compose.
 */
@Preview
@Composable
fun DoctorDashboardScreenPreview() {
    val navController = rememberNavController()
    val routeData = DoctorDashboardScreen(User.Doctor(id = "id", firstName = "", lastName = ""))
    DoctorDashboardScreen(
        routeData = routeData,
        viewModel = viewModel(factory = viewModelFactory {
            initializer {
                DoctorDashboardScreenViewModel(
                    routeData,
                    FirebaseDoctorService(),
                    navController
                )
            }
        })
    )
}