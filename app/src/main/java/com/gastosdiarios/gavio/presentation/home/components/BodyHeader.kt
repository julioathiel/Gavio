package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@Composable
fun BodyHeader(viewModel: HomeViewModel, onNavigationMovimientos: () -> Unit) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val limitDia = CurrencyUtils.formattedCurrency(homeUiState.limitePorDia)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.tu_dinero_actual))

        TextButton(
            onClick = { onNavigationMovimientos() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.ir_a_movimientos),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_navigate_next),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

    }


    Column(modifier = Modifier.padding(start = 16.dp)) {
        FormattedCurrencyCash(viewModel)
        Row {
            Text(
                limitDia,
                modifier = Modifier.padding(end = 4.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                stringResource(R.string.por_dia),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}