package com.gastosdiarios.gavio.data.ui_state

import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

data class AjustesUiState(
    val selectedMode: ModeDarkThemeEnum = ModeDarkThemeEnum.MODE_AUTO,
    val securityBiometric: Boolean = false
)