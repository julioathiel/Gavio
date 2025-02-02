package com.gastosdiarios.gavio.presentation.configuration.notifications

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsLoaderData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.ErrorScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.constants.Constants.HORAS_PREDEFINIDAS
import com.gastosdiarios.gavio.data.constants.Constants.MINUTOS_PREDEFINIDOS
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        TopAppBarOnBack(
            title = "Notificacion",
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            onBack = { onBack() }
        )
    }) { paddingValues ->

        when (val state = uiState) {
            UiStateSingle.Loading -> CommonsLoadingScreen()

            is UiStateSingle.Success<*> -> {
                val data = (uiState as UiStateSingle.Success).data
                ContentNotifications(
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel,
                    data = data
                )
            }

            is UiStateSingle.Error -> {
               ErrorScreen(uiState = state, retryOperation = {  }, Modifier)
            }
        }
    }
}

@Composable
fun ContentNotifications(
    modifier: Modifier,
    viewModel: NotificationsViewModel,
    data: UserPreferences?
) {
    var selectedTime by remember {
        mutableStateOf(
            LocalTime.of(
                data?.hour ?: 0,
                data?.minute ?: 0
            )
        )
    }

    var showTimePicker by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {

        CommonsLoaderData(
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            image = R.raw.notification_lottie,
            repeat = false
        )


        Text(
            text = stringResource(id = R.string.recordatorio_titulo),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.recordatorio_body),
            fontSize = 14.sp,
            color = colorResource(id = R.color.grayCuatro),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.padding_altura_boton)),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),// Fondo transparente
            onClick = { showTimePicker = true }
        ) {

            Text(
                text = "$selectedTime",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (showTimePicker) {
            TimePickerSample(
                horaPredefinida = LocalTime.of(data?.hour ?: 0, data?.minute ?: 0),
                onSelected = { time ->
                    selectedTime = time
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Espacio para empujar los botones al final

        //confirmar la hora seleccionada
        Button(
            onClick = {
                // Llamar a la función del ViewModel para establecer la notificaciion
                viewModel.confirmSelectedTime(
                    selectedTime.hour,
                    selectedTime.minute
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.padding_altura_boton)),

            ) {
            Text(text = stringResource(id = R.string.confirmar))
        }

        // Resetear la hora seleccionada
        Button(
            onClick = {
                selectedTime = LocalTime.of(HORAS_PREDEFINIDAS, MINUTOS_PREDEFINIDOS)
                viewModel.confirmSelectedTime(
                    selectedTime.hour,
                    selectedTime.minute
                )
                //cierra el dialogo
                showTimePicker = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.padding_altura_boton))
                .padding(vertical = 8.dp),
            border = BorderStroke(0.dp, Color.Transparent), // Borde transparente
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),// Fondo transparente
        ) {
            Text(
                text = stringResource(R.string.resetear),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSample(
    horaPredefinida: LocalTime,
    onSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = horaPredefinida.hour,
        initialMinute = horaPredefinida.minute,
        is24Hour = true
    )

    Log.d("alarma", "horaPredefinida: ${horaPredefinida.hour}")
    Log.d("alarma", "horaPredefinida: ${horaPredefinida.minute}")

    var finalTime by remember { mutableStateOf("") }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimePickerDialog(
            onCancel = { onDismiss() },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                finalTime = formatter.format(cal.time)
                val selectedTime = LocalTime.of(state.hour, state.minute)
                Log.d("alarma", "Hora seleccionada: $selectedTime")
                //enviando la hora seleccionada
                onSelected(selectedTime)

                onDismiss()
            },
        ) {
            TimeInput(state = state)
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    TextButton(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}