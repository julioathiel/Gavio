package com.gastosdiarios.gavio.data.ui_state

data class ConfigurationUiState(
    val showBottomSheet: Boolean = false,
    val showShareApp: Boolean = false,
    val resetPending: Boolean = false,
    val resetComplete: Boolean = false,
    val uiState: Boolean = false,
    val sharedLink: String = ""
)
