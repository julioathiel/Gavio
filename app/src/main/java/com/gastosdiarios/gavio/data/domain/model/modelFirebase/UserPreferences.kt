package com.gastosdiarios.gavio.data.domain.model.modelFirebase

import com.gastosdiarios.gavio.data.domain.enums.ThemeMode

data class UserPreferences(
    val userId: String? = null,
    val biometricSecurity: Boolean? = null,
    val limitMonth: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val themeMode: ThemeMode? = null
)
