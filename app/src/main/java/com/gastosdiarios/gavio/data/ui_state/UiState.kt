package com.gastosdiarios.gavio.data.ui_state

sealed interface UiState<T> {
    data class Error(val throwable: Throwable) : UiState<Nothing>
}