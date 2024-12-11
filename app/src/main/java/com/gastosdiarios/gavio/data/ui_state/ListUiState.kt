package com.gastosdiarios.gavio.data.ui_state

data class ListUiState<T>(
    val isLoading: Boolean = false,
    val isUpdateItem: Boolean = false,
    val items: List<T> = emptyList()
)