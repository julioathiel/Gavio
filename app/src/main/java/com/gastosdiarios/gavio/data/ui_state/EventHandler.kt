package com.gastosdiarios.gavio.data.ui_state

import com.gastosdiarios.gavio.domain.enums.ThemeMode

sealed class EventHandler {
    data class SelectedDarkThemeMode(val mode: ThemeMode) : EventHandler()
}