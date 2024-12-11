package com.gastosdiarios.gavio.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.gastosdiarios.gavio.data.constants.Constants.HORAS_PREDEFINIDAS
import com.gastosdiarios.gavio.data.constants.Constants.MINUTOS_PREDEFINIDOS
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.DarkMode
import com.gastosdiarios.gavio.domain.model.FechaMaximaDataModel
import com.gastosdiarios.gavio.domain.model.FechaMaximaDataModel.Companion.selectedOptionKey
import com.gastosdiarios.gavio.domain.model.FechaMaximaDataModel.Companion.selectedSwitchOptionKey
import com.gastosdiarios.gavio.domain.model.TimeDataModel
import com.gastosdiarios.gavio.domain.model.TimeDataModel.Companion.selectedHourKey
import com.gastosdiarios.gavio.domain.model.TimeDataModel.Companion.selectedMinuteKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStorePreferences(private val context: Context) {

    //para asegurarse de que solo haya una instancia
   private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("FECHA_MAXIMA")


    // Claves para las preferencias de la hora y el minuto
    private val Context.selectedHourKey: DataStore<Preferences> by preferencesDataStore("SELECTED_HOUR")

    private val Context.viewPagerShow: DataStore<Preferences> by preferencesDataStore("VIEW_PAGER")
    private val showViewPagerKey = booleanPreferencesKey("SHOW_PAGER")

    private val Context.darkModeStore: DataStore<Preferences> by preferencesDataStore("DARK_MODE")
    private val darkModeKey = booleanPreferencesKey("dark_mode")


    suspend fun updateDarkMode(mode: ModeDarkThemeEnum) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = mode.name // Guardar comoString
        }
    }
    val darkModeFlows: Flow<DarkMode>
        get() = context.dataStore.data.map { preferences ->
            val modeName = preferences[PreferencesKeys.DARK_MODE] ?: ModeDarkThemeEnum.MODE_AUTO.name
            val mode = ModeDarkThemeEnum.valueOf(modeName) // Convertir de String a enum
            DarkMode(mode)
        }

    //para recibir el dia maximo guardada
    fun getFechaMaximoMes(): Flow<FechaMaximaDataModel> = context.dataStore.data
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
                numeroGuardado = preferences[selectedOptionKey] ?: "31",
                switchActivado = preferences[selectedSwitchOptionKey] ?: true
            )
        }

    //para guardar el dia maximo
    suspend fun setSelectedOption(option: String, selectedSwitchOption: Boolean) {
        context.dataStore.edit { preferences: MutablePreferences ->
            if (option != null) {
                preferences[selectedOptionKey] = option
                preferences[selectedSwitchOptionKey] = selectedSwitchOption
            } else {
                preferences.remove(selectedOptionKey)
                preferences.remove(selectedSwitchOptionKey)
            }
        }
    }


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

    fun getViewPager(): Flow<Boolean> = context.viewPagerShow.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Obtener el valor de viewPagerShow, si no existe o es nulo, devolver false
            preferences[showViewPagerKey] ?: false
        }


    suspend fun setViewPagerShow(value: Boolean) {
        context.viewPagerShow.edit { preferences ->
            preferences[showViewPagerKey] = value
        }
    }
}
