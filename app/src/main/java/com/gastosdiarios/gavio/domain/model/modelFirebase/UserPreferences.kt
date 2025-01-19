package com.gastosdiarios.gavio.domain.model.modelFirebase

import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

data class UserPreferences(
    val securityBiometric: Boolean? = null,
    val darkMode: ModeDarkThemeEnum? = null,
    val fechaMaximaNumero: String? = null,
    val fechaMaximaSwitch: Boolean? = null,
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null
)
