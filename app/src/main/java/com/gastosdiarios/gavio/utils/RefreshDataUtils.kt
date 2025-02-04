package com.gastosdiarios.gavio.utils

import com.gastosdiarios.gavio.data.domain.model.RefreshDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

object RefreshDataUtils {
    fun refreshData(
        viewModelScope: CoroutineScope,
        isRefreshing: MutableStateFlow<com.gastosdiarios.gavio.data.domain.model.RefreshDataModel>,
        dataLoading: suspend () -> Unit
    ) {
        viewModelScope.launch {
            isRefreshing.update { it.copy(isRefreshing = true) }
            try {
                delay(2.seconds) // Retraso opcional
                dataLoading() // Ejecutar la lógica de carga de datos
            } catch (e: Exception) {
                // Manejar el error
                // _snackbarMessage.value = R.string.error_message
            } finally {
                isRefreshing.update { it.copy(isRefreshing = false) }
            }
        }
    }
}