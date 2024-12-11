package com.gastosdiarios.gavio.data.ui_state

data class TransactionsUiState(
    val isClicked: Boolean = false,
    val isLongPressed: Boolean = false,
    val isExpanded: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val showConfirmationEditar: Boolean = false,
    val showBottomSheet : Boolean = false
)
