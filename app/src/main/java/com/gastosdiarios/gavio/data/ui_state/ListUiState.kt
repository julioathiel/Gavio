package com.gastosdiarios.gavio.data.ui_state

data class ListUiState<T>(
    val isLoading: Boolean = false,
    val items: List<T> = emptyList(),
    val empty: Boolean = false,
    val update: Boolean = false,
    val error: String? = null,
)