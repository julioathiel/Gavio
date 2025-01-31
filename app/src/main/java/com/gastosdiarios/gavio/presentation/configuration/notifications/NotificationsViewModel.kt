package com.gastosdiarios.gavio.presentation.configuration.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.NotificationProgrammed
import com.gastosdiarios.gavio.data.constants.Constants.NOTIFICATION_ID
import com.gastosdiarios.gavio.data.ui_state.UiStateSimple
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val _uiState = MutableStateFlow<UiStateSimple<UserPreferences?>>(UiStateSimple.Loading)
    val uiState = _uiState.onStart { getTime() }
        .catch { throwable ->
            _uiState.update { UiStateSimple.Error(throwable.message ?: "Error desconocido") }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateSimple.Loading)

    private fun getTime() {
        viewModelScope.launch {
            val data = dbm.getUserPreferences()
            _uiState.update { UiStateSimple.Success(data) }
        }
    }

    fun notificationProgrammed(
        hour: Int? = null,
        minute: Int? = null
    ) {
        // Verificar si el usuario especificó una hora y un minuto, de lo contrario, usar los predeterminados
        val finalHour = hour ?: 21
        val finalMinute = minute ?: 0
        // Configurar la alarma para la hora especificada por el usuario o para las 21:00 horas si no se especifica ninguna hora y minuto
        settingAlarm(finalHour, finalMinute)
    }

    private fun settingAlarm(
        hour: Int,
        minute: Int
    ) {
        val intent = Intent(context, NotificationProgrammed::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Configurar la alarma para la hora y el minuto seleccionados
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val (selectedCalendar) = adjustSelectedTime(hour, minute)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            //
        } else {
            try {
                // Configurar la alarma para que se repita todos los días a la misma hora
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    selectedCalendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                Log.d("alarma", "Error de seguridad al configurar la alarma: ${e.message}")
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
        setHoraMinuto(hour, minute)
        // Obtener la hora y el minuto de selectedTime
        notificationProgrammed(hour, minute)

        val (selectedCalendar, currentCalendar) = adjustSelectedTime(hour, minute)

        val differenceInMillis = selectedCalendar.timeInMillis - currentCalendar.timeInMillis

        val seconds: Long = (differenceInMillis / 1000)
        val minutes: Long = seconds / 60
        val hours: Long = minutes / 60
        val remainingMinutes: Long = minutes % 60
        val remainingSeconds: Long = seconds % 60

        Log.d("alarma", "Horas: $hours")
        Log.d("alarma", "Minutos: $remainingMinutes")
        Log.d("alarma", "Segundos: $remainingSeconds")

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
                    is UiStateSimple.Success -> currentState.data
                    else -> null
                }
                if (currentData != null) {
                    try {
                        val data = currentData.copy(hour = hour, minute = minute)
                        dbm.updateHourMinute(data.hour?: 0, data.minute?: 0)
                    }catch (e:Exception){
                        _uiState.update { UiStateSimple.Error("Error al guardar en Firebase: ${e.message ?: "Error desconocido"}") }
                    }
                }
            }catch (e:Exception){
                _uiState.update { UiStateSimple.Error(e.message ?: "Error desconocido") }
            }
        }
    }

    private fun formatTime(value: Long) = String.format(Locale.US, "%02d", value)
}