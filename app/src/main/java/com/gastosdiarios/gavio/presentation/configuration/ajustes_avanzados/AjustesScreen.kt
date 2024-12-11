package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.ui_state.EventHandler
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

@Composable
fun AjustesScreen(viewModel: AjustesViewModel) {
    // Recolecta el estado del flujo de booleanos
    val state = viewModel.selectedMode.value

    Scaffold(topBar = { ToolbarAjustes(state) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            var selectedMode by remember { mutableStateOf(viewModel.selectedMode.value) }

            ModeDarkThemeEnum.entries.forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val title = when (mode) {
                        ModeDarkThemeEnum.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                        ModeDarkThemeEnum.MODE_DAY -> stringResource(id = R.string.mode_day)
                        ModeDarkThemeEnum.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                    }
                    Text(text = title, modifier = Modifier.weight(1f))
                    RadioButton(selected = selectedMode == mode, onClick = { selectedMode = mode })
                }
            }
            val darkSystem = isSystemInDarkTheme()
            val isDarkTheme by remember(LocalConfiguration.current.uiMode) {
                mutableStateOf(
                    darkSystem
                )
            }

            LaunchedEffect(selectedMode, isDarkTheme) {
                when (selectedMode) {
                    ModeDarkThemeEnum.MODE_AUTO -> viewModel.onEventHandler(
                        EventHandler.SelectedDarkThemeMode(
                            ModeDarkThemeEnum.MODE_AUTO
                        )
                    )

                    ModeDarkThemeEnum.MODE_DAY -> viewModel.onEventHandler(
                        EventHandler.SelectedDarkThemeMode(
                            ModeDarkThemeEnum.MODE_DAY
                        )
                    )

                    ModeDarkThemeEnum.MODE_NIGHT -> viewModel.onEventHandler(
                        EventHandler.SelectedDarkThemeMode(
                            ModeDarkThemeEnum.MODE_NIGHT
                        )
                    )
                }
            }

            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarAjustes(state: ModeDarkThemeEnum) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.toolbar_ajustes))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor =  MaterialTheme.colorScheme.background),
        actions = {
            Icon(
                painter = painterResource(
                    id = when (state) {
                        ModeDarkThemeEnum.MODE_AUTO -> R.drawable.ic_rounded_routine
                        ModeDarkThemeEnum.MODE_DAY -> R.drawable.ic_light_mode
                        ModeDarkThemeEnum.MODE_NIGHT -> R.drawable.ic_dark_mode
                    }
                ),
                contentDescription = when (state) {
                    ModeDarkThemeEnum.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                    ModeDarkThemeEnum.MODE_DAY -> stringResource(id = R.string.mode_day)
                    ModeDarkThemeEnum.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                }.toString(), modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}