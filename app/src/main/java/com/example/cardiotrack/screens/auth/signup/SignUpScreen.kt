package com.example.cardiotrack.screens.auth.signup


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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import com.example.cardiotrack.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

@Serializable
data object SignUpScreen

@Composable
fun SignUpScreen(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )
        OutlinedTextField(
            label = { Text("E-mail") },
            value = state.email,
            onValueChange = viewModel::handleEmailChange,
            enabled = !state.loading,
            isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
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
        OutlinedTextField(
            label = { Text("Powtórz hasło") },
            value = state.passwordRepeat,
            onValueChange = viewModel::handlePasswordRepeatChange,
            enabled = !state.loading,
            isError = state.passwordRepeatError != null,
            supportingText = state.passwordRepeatError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = {
                TransformedText(
                    AnnotatedString("*".repeat(it.text.length)), OffsetMapping.Identity
                )
            },
        )
        OutlinedTextField(
            label = { Text("Imie") },
            value = state.firstName,
            onValueChange = viewModel::handleFirstNameChange,
            enabled = !state.loading,
            isError = state.firstNameError != null,
            supportingText = state.firstNameError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            label = { Text("Nazwisko") },
            value = state.lastName,
            onValueChange = viewModel::handleLastNameChange,
            enabled = !state.loading,
            isError = state.lastNameError != null,
            supportingText = state.lastNameError?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.padding(20.dp))
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignUp
        ) {
            Text("Zarejestruj się")
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignIn
        ) {
            Text("Zaloguj się")
        }
    }
}
//@Composable
//fun TwojEktran() {
    // tutaj piszesz jak wyglada twoj ekran
//}

//@Preview
//@Composable
//fun PreviewTwojegoEkranu() {
    //TwojEktran()
//}