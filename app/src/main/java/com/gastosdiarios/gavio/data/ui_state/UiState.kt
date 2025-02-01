package com.gastosdiarios.gavio.data.ui_state

sealed interface UiStateList<out T> {
    data object Loading : UiStateList<Nothing>
    data object Empty : UiStateList<Nothing>
    data class Success<T>(val data: List<T>) : UiStateList<T>
    data class Error(val message: String, val throwable: Throwable) : UiStateList<Nothing>
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Empty : Result<Nothing>()
}

sealed interface UiStateSingle<out T> {
    data object Loading : UiStateSingle<Nothing>
    data class Success<T>(val data: T) : UiStateSingle<T>
    data class Error(val message: String) : UiStateSingle<Nothing>
}