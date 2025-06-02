package com.example.cardiotrack.screens.auth.signin


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable

@Serializable
data object SignInScreen

@Composable
fun SignInScreen(viewModel: SignInScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        OutlinedTextField(
            label = { Text("E-mail") },
            value = state.email,
            onValueChange = viewModel::handleEmailChange,
            enabled = !state.loading,
            isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            label = { Text("Hasło") },
            value = state.password,
            onValueChange = viewModel::handlePasswordChange,
            enabled = !state.loading,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = {
                TransformedText(
                    AnnotatedString("*".repeat(it.text.length)), OffsetMapping.Identity
                )
            },
        )
        Spacer(modifier = Modifier.padding(20.dp))
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignIn
        ) {
            Text("Zaloguj się")
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignUp
        ) {
            Text("Zarejestruj się")
        }
    }
}