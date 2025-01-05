package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Crear los switches de forma dinámica
@Composable
fun SwitchWithText(
    switchText: String,
    numberSwitch: Int,
    selectedSwitchNumber: Int?,
    isActivated: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = switchText, modifier = Modifier.weight(1f))

        Switch(
            checked = selectedSwitchNumber == numberSwitch,
            onCheckedChange = { newState ->
                if (newState) {
                    isActivated(true)
                }
            },
            modifier = Modifier.height(30.dp)
        )
    }

    Spacer(modifier = Modifier.size(20.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.size(20.dp))
}