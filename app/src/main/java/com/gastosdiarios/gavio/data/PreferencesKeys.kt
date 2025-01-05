package com.gastosdiarios.gavio.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val DARK_MODE_KEY = stringPreferencesKey("DARK_MODE")
    val SECURITY_BIOMETRIC_KEY = booleanPreferencesKey("SEGURIDAD_BIOMETRICA")
    val SELECTED_OPTION_KEY = stringPreferencesKey("FECHA_MAXIMA")
    val SELECTED_SWITCH_OPTION_KEY = booleanPreferencesKey("FECHA_MAXIMA_BOOLEAN")
}