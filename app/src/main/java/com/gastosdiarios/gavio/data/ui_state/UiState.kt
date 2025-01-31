package com.gastosdiarios.gavio.data.ui_state

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object IsEmpty : UiState<Nothing>
    data class Success<T>(val data: List<T>) : UiState<T>
    data class Error(val message: String, val throwable: Throwable) : UiState<Nothing>
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Empty : Result<Nothing>()
}

sealed interface UiStateSimple<out T> {
    data object Loading : UiStateSimple<Nothing>
    data class Success<T>(val data: T) : UiStateSimple<T>
    data class Error(val message: String) : UiStateSimple<Nothing>
}