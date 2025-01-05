package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.domain.enums.ItemConfAvanzada
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

@Composable
fun AjustesScreen(viewModel: AjustesViewModel = hiltViewModel(), onBack: () -> Unit) {
    // Recolecta el estado del flujo de booleanos
    val uiState by viewModel.uiState.collectAsState()
    var isChecked by remember { mutableStateOf(uiState.securityBiometric) }

    Scaffold(
        topBar = {
            TopAppBarOnBack(
                title = stringResource(id = R.string.toolbar_ajustes),
                containerColor = MaterialTheme.colorScheme.surface,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onBack = { onBack() },
                actions = {
                    Icon(
                        painter = painterResource(
                            id = when (uiState.selectedMode) {
                                ModeDarkThemeEnum.MODE_AUTO -> R.drawable.ic_rounded_routine
                                ModeDarkThemeEnum.MODE_DAY -> R.drawable.ic_light_mode
                                ModeDarkThemeEnum.MODE_NIGHT -> R.drawable.ic_dark_mode
                            }
                        ),
                        contentDescription = when (uiState.selectedMode) {
                            ModeDarkThemeEnum.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                            ModeDarkThemeEnum.MODE_DAY -> stringResource(id = R.string.mode_day)
                            ModeDarkThemeEnum.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                        }.toString(), modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {

                ModeDarkThemeEnum.entries.forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val title = when (mode) {
                            ModeDarkThemeEnum.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                            ModeDarkThemeEnum.MODE_DAY -> stringResource(id = R.string.mode_day)
                            ModeDarkThemeEnum.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                        }
                        Text(text = title, modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = uiState.selectedMode == mode,
                            onClick = { viewModel.setThemeMode(mode) }
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.size(20.dp))
                ItemConfAvanzada.entries.forEach { item ->
                    when (item) {
                        ItemConfAvanzada.SEGURIDAD -> {
                            SwitchWith(
                                switchText = "Seguridad",
                                isChecked = isChecked,
                                onCheckedChange = { newState ->
                                    isChecked = newState
                                    viewModel.setBiometric(newState)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchWith(
    switchText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = switchText, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.height(30.dp)
        )
    }

    Spacer(modifier = Modifier.size(20.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.size(20.dp))
}