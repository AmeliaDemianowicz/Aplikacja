package com.example.cardiotrack.screens.doctor.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.doctor.FirebaseDoctorService
import kotlinx.serialization.Serializable

@Serializable
data class DoctorDashboardScreen(val user: User.Doctor)

@Composable
fun DoctorDashboardScreen(
    routeData: DoctorDashboardScreen,
    viewModel: DoctorDashboardScreenViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
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
            .padding(30.dp)
    ) {
        OutlinedTextField(
            label = { Text("Wyszukaj pacjenta") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            value = state.fullNameSearchTerm,
            onValueChange = viewModel::handleSearchTermChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(state.patients, key = { it.id }) {
                Text("${it.firstName} ${it.lastName}", modifier = Modifier.padding(8.dp))
            }

            if (state.loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun DoctorDashboardScreenPreview() {
    DoctorDashboardScreen(
        routeData = DoctorDashboardScreen(User.Doctor(id = "id")),
        viewModel = viewModel(factory = viewModelFactory {
            initializer { DoctorDashboardScreenViewModel(FirebaseDoctorService()) }
        })
    )
}