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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences

@Composable
fun AjustesScreen(
    viewModel: AjustesViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState: UserPreferences by viewModel.uiState.collectAsState()
    var isSecurity:Boolean by remember { mutableStateOf(uiState.biometricSecurity ?: false) }
    var checkButton:ThemeMode by remember { mutableStateOf(uiState.themeMode ?: ThemeMode.MODE_AUTO) }

    LaunchedEffect(key1 = uiState) {
        isSecurity = uiState.biometricSecurity ?: false
        checkButton = uiState.themeMode ?: ThemeMode.MODE_AUTO
    }


    Scaffold(
        topBar = {
            TopAppBarOnBack(
                title = stringResource(id = R.string.toolbar_ajustes),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onBack = { onBack() },
                actions = {
                    Icon(
                        painter = painterResource(
                            id = when (uiState.themeMode) {
                                ThemeMode.MODE_AUTO -> R.drawable.ic_rounded_routine
                                ThemeMode.MODE_DAY -> R.drawable.ic_light_mode
                                ThemeMode.MODE_NIGHT -> R.drawable.ic_dark_mode
                                null -> R.drawable.ic_rounded_routine
                            }
                        ),
                        contentDescription = when (uiState.themeMode) {
                            ThemeMode.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                            ThemeMode.MODE_DAY -> stringResource(id = R.string.mode_day)
                            ThemeMode.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                            null -> stringResource(id = R.string.mode_auto)
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

                ThemeMode.entries.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val title = when (option) {
                            ThemeMode.MODE_AUTO -> stringResource(id = R.string.mode_auto)
                            ThemeMode.MODE_DAY -> stringResource(id = R.string.mode_day)
                            ThemeMode.MODE_NIGHT -> stringResource(id = R.string.mode_night)
                        }
                        Text(text = title, modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = option == checkButton,
                            onClick = {
                                checkButton = option
                                viewModel.updateThemeMode(checkButton) }
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
                                isChecked = isSecurity,
                                onCheckedChange = { newState ->
                                    isSecurity = newState
                                    viewModel.updateBiometricSecurity(isSecurity)
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