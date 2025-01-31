package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.components.SwitchWithText
import kotlinx.coroutines.launch

@Composable
fun ActualizarMaximoFechaScreen(
    viewModel: ActualizarMaximoFechaViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val isShowSnackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBarOnBack(
                title = stringResource(id = R.string.toolbar_cambio_fecha),
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = { onBack() },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = isShowSnackbar)
        },
        content = { innerPadding ->
            Content(Modifier.padding(innerPadding), viewModel, isShowSnackbar)
        }
    )
}


@Composable
fun Content(
    modifier: Modifier,
    viewModel: ActualizarMaximoFechaViewModel,
    isShowSnackbar: SnackbarHostState
    ) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    var isOptionSelected by remember { mutableStateOf(false) }
    var selectedSwitchNumber by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.selectedOption) {
        selectedSwitchNumber = uiState.selectedOption
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(R.string.elige_una_opcion),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 24.sp
                    )
                ) {
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.grayCuatro)
                    )
                ) {
                    append(stringResource(R.string._15_dias_para_cobros_quinccenales))
                    append(stringResource(R.string._31_dias_para_cobros_mensuales))
                    append(stringResource(R.string._60_dias_para_cobros_cada_dos_meses))
                    append(stringResource(R.string._90_dias_para_cobros_cada_3_meses))
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.size(20.dp))

        SwitchWithText(
            stringResource(id = R.string.quincena),
            numberSwitch = 15,
            selectedSwitchNumber
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 15
                isOptionSelected = true
            }
        }
        SwitchWithText(
            stringResource(R.string.primer_mes),
            numberSwitch = 31,
            selectedSwitchNumber
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 31
                isOptionSelected = true
            }
        }

        SwitchWithText(
            stringResource(R.string.dos_meses),
            numberSwitch = 60,
            selectedSwitchNumber,
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 60
                isOptionSelected = true
            }
        }

        SwitchWithText(
            stringResource(R.string.tres_meses),
            numberSwitch = 90,
            selectedSwitchNumber,
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 90
                isOptionSelected = true
            }
        }

        Spacer(
            modifier = Modifier
                .size(30.dp)
                .weight(1f)
        )

        Button(
            onClick = {
                if (isOptionSelected) {
                    viewModel.updateFechaMaxima(selectedSwitchNumber)
                    scope.launch {
                        isShowSnackbar.showSnackbar("Opción guardada correctamente")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.padding_altura_boton))
        ) {
            Text(text = stringResource(R.string.confirmar))
        }
        Spacer(modifier = Modifier.size(30.dp))
    }
}
