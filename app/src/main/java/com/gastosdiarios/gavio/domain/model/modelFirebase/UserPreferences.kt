package com.gastosdiarios.gavio.domain.model.modelFirebase

import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum

data class UserPreferences(
    val userId: String? = null,
    val biometricSecurity: Boolean? = null,
    val dateMax: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val themeMode: ModeDarkThemeEnum? = null
)
