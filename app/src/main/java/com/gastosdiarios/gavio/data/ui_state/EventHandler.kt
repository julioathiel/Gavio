package com.gastosdiarios.gavio.data.ui_state

import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

sealed class EventHandler {
    data class SelectedDarkThemeMode(val mode: ModeDarkThemeEnum) : EventHandler()
}