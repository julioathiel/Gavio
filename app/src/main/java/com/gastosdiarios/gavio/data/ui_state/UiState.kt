package com.gastosdiarios.gavio.data.ui_state

sealed interface UiState<T> {
    data class Error(val throwable: Throwable) : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Empty : Result<Nothing>()
}