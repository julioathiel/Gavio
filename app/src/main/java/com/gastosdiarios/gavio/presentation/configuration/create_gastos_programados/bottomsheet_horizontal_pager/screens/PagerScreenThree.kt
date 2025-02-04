package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.data.domain.model.Time
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagerScreenThree(item: GastosProgramadosModel, time: (MutableState<Time>) -> Unit) {
    var selectedTime by remember { mutableStateOf(LocalTime.of(item.hour ?: 0, item.minute ?: 0)) }

    val state = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = true
    )
    var showTimePicker by remember { mutableStateOf(false) }

    val title = "Select Time"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Añadimos padding alrededor del Column
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Spacer(modifier = Modifier.height(4.dp))

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
                TimeInput(state = state)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    // toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }

                    TextButton(onClick = {
                        selectedTime = LocalTime.of(state.hour, state.minute)
                        showTimePicker = true
                    }
                        //  onConfirm
                    ) {
                        Text("OK")
                    }
                }
            }
        }
        if (showTimePicker) {
            Text("Hora seleccionada: ${selectedTime.hour}:${selectedTime.minute}")
        }
        LaunchedEffect(selectedTime) {
            time(
                mutableStateOf(
                    Time(
                        hour = selectedTime.hour,
                        minute = selectedTime.minute
                    )
                )
            )
        }
    }
}