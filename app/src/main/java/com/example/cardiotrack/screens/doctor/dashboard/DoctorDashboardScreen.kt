package com.example.cardiotrack.screens.doctor.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.cardiotrack.R
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.doctor.FirebaseDoctorService
import kotlinx.serialization.Serializable

@Serializable
data class DoctorDashboardScreen(val user: User.Doctor)

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
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo aplikacji",
                    modifier = Modifier.padding(4.dp)
                )
            })

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil pacjenta",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("${it.firstName} ${it.lastName}")
                    Text("${it.birthDate} ${it.sex}")
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