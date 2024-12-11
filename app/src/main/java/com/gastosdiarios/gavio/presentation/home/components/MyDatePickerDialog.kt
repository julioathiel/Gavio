package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.home.HomeViewModel

@Composable
fun MyDatePickerDialog(homeViewModel: HomeViewModel) {
    var selectedDate: String? by remember { mutableStateOf(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    TextButton(onClick = { showDatePicker = true }) {
        Text(
            text = stringResource(id = R.string.editar),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    if (showDatePicker) {
        DatePickerView(
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false },
            homeViewModel
        )
    }
    //selectedDate get la fecha ··/··/····
    selectedDate?.let { homeViewModel.sendDateElegida(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    val state = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return viewModel.isDateSelectable(utcTimeMillis, homeUiState.selectedOptionFechaMaxima)
        }
    })

   val selectedDate = viewModel.formatSelectedDate(state.selectedDateMillis)

    DatePickerDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    ) {
        DatePicker(state = state)
    }
}