package com.gastosdiarios.gavio.presentation.welcome.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.welcome.register.components.ButtonRegister
import com.gastosdiarios.gavio.presentation.welcome.register.components.EmailRegisterField
import com.gastosdiarios.gavio.presentation.welcome.register.components.PasswordRegisterField
import com.gastosdiarios.gavio.ui.theme.ColorBlack
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onToLogin: (email: String, password: String) -> Unit) {
    val uiState by viewModel.uiState
    val registerState = viewModel.registerState.value

    val auth = FirebaseAuth.getInstance()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.crear_cuenta),
                    color = MaterialTheme.colorScheme.background
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onBackground)
        )
    }, containerColor = ColorBlack) { paddingValues: PaddingValues ->
        val context = LocalContext.current
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = stringResource(id = R.string.email),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            EmailRegisterField(
                valueEmail = uiState.email,
                onNewValue = viewModel::onEmailChange,
                Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = stringResource(id = R.string.password),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            PasswordRegisterField(
                valuePassword = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.password_error),
                color = Color.White, style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))

            ButtonRegister(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                title = stringResource(id = R.string.crear_cuenta),
                onClick = {
                    viewModel.onCreandoCuenta(context, uiState.email, uiState.password)
                }
            ) { viewModel.enabledButtonRegister() }

            LaunchedEffect(key1 = registerState) { // Ejecutar onToLogin() cuando registerState sea true
                if (registerState) {
                    onToLogin(uiState.email, uiState.password)
                }
            }
        }
    }
}