package com.gastosdiarios.gavio.data.ui_state

import com.gastosdiarios.gavio.domain.enums.ThemeMode

data class AjustesUiState(
    val selectedMode: ThemeMode = ThemeMode.MODE_AUTO,
    val securityBiometric: Boolean = false
)