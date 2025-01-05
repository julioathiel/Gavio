package com.gastosdiarios.gavio.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.gastosdiarios.gavio.data.constants.Constants.HORAS_PREDEFINIDAS
import com.gastosdiarios.gavio.data.constants.Constants.MINUTOS_PREDEFINIDOS
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.DarkMode
import com.gastosdiarios.gavio.domain.model.FechaMaximaDataModel
import com.gastosdiarios.gavio.domain.model.SeguridadBiometrica
import com.gastosdiarios.gavio.domain.model.TimeDataModel
import com.gastosdiarios.gavio.domain.model.TimeDataModel.Companion.selectedHourKey
import com.gastosdiarios.gavio.domain.model.TimeDataModel.Companion.selectedMinuteKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStorePreferences(private val context: Context) {
    //para asegurarse de que solo haya una instancia
    private val Context.fechaMaxima: DataStore<Preferences> by preferencesDataStore("FECHA_MAXIMA")
    private val Context.darkMode: DataStore<Preferences> by preferencesDataStore("DARK_MODE")
    private val Context.selectedHourKey: DataStore<Preferences> by preferencesDataStore("SELECTED_HOUR")
    private val Context.seguridadBiometrica: DataStore<Preferences> by preferencesDataStore("SEGURIDAD_BIOMETRICA")



    fun getBiometric(): Flow<SeguridadBiometrica> = context.seguridadBiometrica.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SeguridadBiometrica(preferences[PreferencesKeys.SECURITY_BIOMETRIC_KEY] ?: false)
        }

    suspend fun setBiometric(biometric: SeguridadBiometrica) {
        context.seguridadBiometrica.edit { preferences ->
            preferences[PreferencesKeys.SECURITY_BIOMETRIC_KEY] = biometric.securityActivated
        }
    }

//------------------------------modo oscuro, auto, claro-------------------------------------------------------
    suspend fun updateDarkMode(mode: ModeDarkThemeEnum) {
        context.darkMode.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_KEY] = mode.name // Guardar comoString
        }
    }

    fun getThemeMode(): Flow<DarkMode> = context.darkMode.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
        val modeName = it[PreferencesKeys.DARK_MODE_KEY] ?: ModeDarkThemeEnum.MODE_AUTO.name
        val mode = ModeDarkThemeEnum.valueOf(modeName)
        DarkMode(mode)
    }


    //-----------------------------switch de fecha maxima------------------------------------------------
    //para recibir el dia maximo guardada
    fun getFechaMaximoMes(): Flow<FechaMaximaDataModel> = context.fechaMaxima.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            FechaMaximaDataModel(
                numeroGuardado = preferences[PreferencesKeys.SELECTED_OPTION_KEY] ?: "31",
                switchActivado = preferences[PreferencesKeys.SELECTED_SWITCH_OPTION_KEY] ?: true
            )
        }

    //para guardar el dia maximo
    suspend fun setSelectedOption(option: String, selectedSwitchOption: Boolean) {
        context.fechaMaxima.edit { preferences: MutablePreferences ->
            if (option != null) {
                preferences[PreferencesKeys.SELECTED_OPTION_KEY] = option
                preferences[PreferencesKeys.SELECTED_SWITCH_OPTION_KEY] = selectedSwitchOption
            } else {
                preferences.remove(PreferencesKeys.SELECTED_OPTION_KEY)
                preferences.remove(PreferencesKeys.SELECTED_SWITCH_OPTION_KEY)
            }
        }
    }

    //----------------------------------Hora Minuto---------------------------------------------------
    // Recuperar la hora y el minuto seleccionados
    fun getHoraMinuto(): Flow<TimeDataModel> = context.selectedHourKey.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            TimeDataModel(
                // Obtener la hora guardada, por defecto 21 si no hay valor
                hour = preferences[selectedHourKey] ?: HORAS_PREDEFINIDAS,
                // Obtener el minuto guardado, por defecto 0 si no hay valor
                minute = preferences[selectedMinuteKey] ?: MINUTOS_PREDEFINIDOS
            )
        }

    suspend fun setHoraMinuto(hour: Int?, minute: Int?) {
        context.selectedHourKey.edit { preferences ->
            if (hour != null) {
                preferences[selectedHourKey] = hour
            }
            if (minute != null) {
                preferences[selectedMinuteKey] = minute
            }
        }
    }

}
