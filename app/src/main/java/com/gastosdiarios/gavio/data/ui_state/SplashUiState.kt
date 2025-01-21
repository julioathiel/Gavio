package com.gastosdiarios.gavio.data.ui_state

data class SplashUiState(
    val securityActivated: Boolean = false,
    val isLoading: Boolean = false,
    val isActivated: Boolean = false,
    val userRegistered: Boolean = true,
    val primerInicioSesion: Boolean = false,
    val startDestination: String? = null
)