package com.gastosdiarios.gavio.presentation.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R

@Composable
fun BiometricScreen(
    activity: AppCompatActivity,
    viewModel: BiometricViewModel = hiltViewModel(),
    onToHomeScreen: () -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = "bloqueado",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 88.dp)
                .size(35.dp),
            // colorFilter = ColorFilter.tint(color = Color(0xFF1B8755))
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = "Gavio bloqueado",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp)
        )
        Spacer(modifier = Modifier.size(200.dp))
        TextButton(
            onClick = {
                huellaDigital(viewModel, activity, onToHomeScreen)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = "Desbloquear",
                //color = Color(0xFF1B8755)
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        huellaDigital(viewModel, activity, onToHomeScreen)
    }

}

fun huellaDigital(
    viewModel: BiometricViewModel,
    activity: AppCompatActivity,
    onToHomeScreen: () -> Unit,
) {
    viewModel.showBiometricPrompt(
        title = "Desbloquea Gavio para poder usarlo",
        subtitle = "",
        activity = activity,
        auth = {
            if (it) {
                onToHomeScreen()
            }
        }
    )
}