package com.gastosdiarios.gavio.domain.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class FechaMaximaDataModel(val numeroGuardado: String, val switchActivado: Boolean){
    companion object {
        val selectedOptionKey = stringPreferencesKey("FECHA_MAXIMA")
        val selectedSwitchOptionKey = booleanPreferencesKey("FECHA_MAXIMA_BOOLEAN")
    }
}