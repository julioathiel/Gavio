package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsToolbar
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.components.SwitchWithText
import kotlinx.coroutines.launch

@Composable
fun ActualizarMaximoFechaScreen(
    viewModel: ActualizarMaximoFechaViewModel
) {

    val isShowSnackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CommonsToolbar(
                title = stringResource(id = R.string.toolbar_cambio_fecha),
                colors = MaterialTheme.colorScheme.background
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = isShowSnackbar)
        },
        content = { padding ->
            Content(padding, viewModel, isShowSnackbar)
        }
    )
}


@Composable
fun Content(
    paddingValues: PaddingValues,
    viewModel: ActualizarMaximoFechaViewModel,
    isShowSnackbar: SnackbarHostState,

    ) {
    val selectedOptionState by viewModel.selectedOption.observeAsState()
    val selectedSwitchOption = viewModel.selectedSwitchOption.value
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.padding(30.dp))
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
                    append(stringResource(R.string._31_d_as_para_cobros_mensuales))
                    append(stringResource(R.string._60_d_as_para_cobros_cada_dos_meses))
                    append(stringResource(R.string._90_d_as_para_cobros_cada_3_meses))
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.size(20.dp))

        var isPrueba by remember { mutableStateOf(false) }
        var selectedSwitchNumber by remember { mutableStateOf(selectedOptionState) }
        SwitchWithText(
            stringResource(R.string.primer_mes),
            numberSwitch = 31,
            selectedSwitchNumber
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 31
                isPrueba = true
            }
        }

        SwitchWithText(
            stringResource(R.string.dos_meses),
            numberSwitch = 60,
            selectedSwitchNumber,
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 60
                isPrueba = true
            }
        }

        SwitchWithText(
            stringResource(R.string.tres_meses),
            numberSwitch = 90,
            selectedSwitchNumber,
        ) { isActivated ->
            if (isActivated) {
                selectedSwitchNumber = 90
                isPrueba = true
            }
        }

        Spacer(
            modifier = Modifier
                .size(30.dp)
                .weight(1f)
        )
        Button(
            onClick = {
                if (isPrueba) {
                    viewModel.setSelectedOption(selectedSwitchNumber!!, selectedSwitchOption)
                    scope.launch {
                        isShowSnackbar.showSnackbar("Opción guardada correctamente")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
        ) {
            Text(text = stringResource(R.string.confirmar))
        }
        Spacer(modifier = Modifier.size(30.dp))
    }
}