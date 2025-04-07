package com.gastosdiarios.gavio.presentation.configuration.notifications

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val dbm: DataBaseManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiStateSingle<com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserPreferences?>>(UiStateSingle.Loading)
    val uiState = _uiState.onStart { getTime() }
        .catch { throwable ->
            _uiState.update { UiStateSingle.Error(throwable = throwable) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateSingle.Loading)

    private fun getTime() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.getUserPreferences().collect{ db ->
                _uiState.update { UiStateSingle.Success(db) }
            }
        }
    }




    private fun adjustSelectedTime(hour: Int, minute: Int): Pair<Calendar, Calendar> {
        val currentCalendar = Calendar.getInstance()
        val selectedCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (selectedCalendar.before(currentCalendar)) {
            //si la alarma esta en el pasado, se suma un dia
            selectedCalendar.add(Calendar.DATE, 1)
        }else{
            //si la alarma esta en el futuro, se suma un dia
            selectedCalendar.add(Calendar.DATE, 0)
        }

        return Pair(selectedCalendar, currentCalendar)
    }

    // Función para confirmar la hora seleccionada
    fun confirmSelectedTime(
        hour: Int,
        minute: Int
    ) {
        //guardando hora y minuto
        val newHour = String.format(Locale.US, "%02d", hour)
        setHoraMinuto(newHour.toInt(), minute)
        // Obtener la hora y el minuto de selectedTime

        val (selectedCalendar, currentCalendar) = adjustSelectedTime(hour, minute)

        val differenceInMillis = selectedCalendar.timeInMillis - currentCalendar.timeInMillis

        val seconds: Long = (differenceInMillis / 1000)
        val minutes: Long = seconds / 60
        val hours: Long = minutes / 60
        val remainingMinutes: Long = minutes % 60
        val remainingSeconds: Long = seconds % 60


        val message: String =
            when {
                differenceInMillis <= 0 -> "La alarma ya ha pasado o está configurada para ahora mismo."
                hours > 0 -> "Faltan: ${formatTime(hours)}:${formatTime(remainingMinutes)}:${formatTime(remainingSeconds)}"
                minutes > 0 -> "Faltan: ${formatTime(remainingMinutes)} minutos y ${formatTime(remainingSeconds)} segundos"
                else -> "Faltan: ${formatTime(seconds)} segundos"
            }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setHoraMinuto(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                val currentData = when (val currentState = _uiState.value) {
                    is UiStateSingle.Success -> currentState.data
                    else -> null
                }
                if (currentData != null) {
                    try {
                        val data = currentData.copy(hour = hour, minute = minute)
                        dbm.updateHourMinute(data.hour?: 0, data.minute?: 0)
                    }catch (e:Exception){
                        _uiState.update { UiStateSingle.Error(throwable = e) }
                    }
                }
            }catch (e:Exception){
                _uiState.update { UiStateSingle.Error(throwable = e) }
            }
        }
    }

    private fun formatTime(value: Long) = String.format(Locale.US, "%02d", value)
}