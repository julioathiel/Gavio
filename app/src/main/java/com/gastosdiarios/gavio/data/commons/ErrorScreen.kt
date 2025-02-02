package com.gastosdiarios.gavio.data.commons

import android.accounts.NetworkErrorException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.data.ui_state.UiError
import java.util.concurrent.TimeoutException

@Composable
fun ErrorScreen(uiState: UiError, retryOperation:() -> Unit, modifier: Modifier) {
    val errorMessage = when ((uiState).throwable) {
        is NetworkErrorException -> "Error de conexión a internet."
        is TimeoutException -> "La operación ha tardado demasiado."
        else -> "Ha ocurrido un error inesperado."
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Llama a la función para reintentar la operación
            retryOperation()
        }) {
            Text("Reintentar")
        }
    }
}