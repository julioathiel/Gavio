package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R

@Composable
fun ObservadorDinero(temporal: CharSequence, decimalPart: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.tipo_moneda),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Bottom).offset(y = (-6).dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = temporal.toString(),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = decimalPart,
            //texto que modifica los decimales en la posicion hacia arriba y se ven pequeños
            modifier = Modifier.align(Alignment.Top).offset(y = (2).dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}