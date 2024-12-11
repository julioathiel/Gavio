package com.gastosdiarios.gavio.domain.model

import androidx.datastore.preferences.core.intPreferencesKey

data class TimeDataModel(
    val hour: Int,
    val minute: Int
){
    companion object {
        val selectedHourKey = intPreferencesKey("SELECTED_HOUR")
        val selectedMinuteKey = intPreferencesKey("SELECTED_MINUTE")
    }
}