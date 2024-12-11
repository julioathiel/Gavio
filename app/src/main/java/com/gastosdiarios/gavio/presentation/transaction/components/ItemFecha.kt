package com.gastosdiarios.gavio.presentation.transaction.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemFecha(fecha: String) {
    Spacer(modifier = Modifier.padding(16.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(30.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = fecha,
            style = MaterialTheme.typography.labelMedium
        )
    }
}