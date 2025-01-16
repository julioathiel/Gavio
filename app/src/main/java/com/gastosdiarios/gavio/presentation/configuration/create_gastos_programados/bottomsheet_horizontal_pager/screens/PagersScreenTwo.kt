package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.utils.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagersScreenTwo(item: GastosProgramadosModel, selectedDate: (Long?) -> Unit) {
    val date by remember{ mutableStateOf(item.date ?: "")}

    Box(modifier = Modifier.fillMaxSize()) {
        val initialDateMillis = if (date.isNotBlank()) {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            null
        }

        val state = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return DateUtils.isDateSelectableRestrictMin(utcTimeMillis)
                }
            }
        )

        LaunchedEffect(state.selectedDateMillis) {
            selectedDate(state.selectedDateMillis)
        }

        DatePicker(state = state, colors = DatePickerDefaults.colors(containerColor = Color.Transparent))
    }
}