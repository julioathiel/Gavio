package com.gastosdiarios.gavio.receiver

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.domain.model.Alarm
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.utils.Constants.ALARM_CHANNEL_NAME
import com.gastosdiarios.gavio.utils.Constants.ALARM_ID
import com.gastosdiarios.gavio.utils.Constants.NOTIFICATION_ID
import com.gastosdiarios.gavio.utils.Constants.STOP_ALARM
import com.gastosdiarios.gavio.utils.cancelAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.internal.notify
import javax.inject.Inject

const val HECHO = "HECHO"
const val RECHAZAR = "RECHAZAR"

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var gastosProgramadosFirestore: GastosProgramadosFirestore

    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderJson = intent?.getStringExtra(ALARM_ID)
        val reminder = reminderJson?.let { Json.decodeFromString<Alarm>(it) }

        val mediaPlayer: MediaPlayer =
            MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer.isLooping = true


        if (intent?.action == STOP_ALARM) {
            val alarmId = intent.getIntExtra(ALARM_ID, 2)
            NotificationManagerCompat.from(context).cancel(alarmId)

            mediaPlayer.release()
            mediaPlayer.stop()

            val pIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pIntent)

            return
        }

        val doneIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID, reminderJson)
            action = HECHO
        }

        val donePendingIntent = PendingIntent.getBroadcast(
            context, reminder?.id?.toInt() ?: 0, doneIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val closeIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID, reminderJson)
            action = RECHAZAR
        }

        val closePendingIntent = PendingIntent.getBroadcast(
            context, reminder?.id?.toInt() ?: 0, closeIntent, PendingIntent.FLAG_IMMUTABLE
        )



        when (intent?.action) {
            HECHO -> { //DONE SIGNIFICA HECHO
                runBlocking {
                    if (reminder != null) {
                        // 1. Obtener el ID del GastosProgramadosModel
                        val gastosProgramadosId = reminder.gastosProgramadosId
                        // 2. Obtener el GastosProgramadosModel por su ID
                        val item = gastosProgramadosFirestore.get()
                            .firstOrNull { it.uid == gastosProgramadosId }
                        // reminder?.copy(isTaken = true)
                        if(item != null){
                            gastosProgramadosFirestore.update(item.copy(select = true))
                        }
                    }
                }
                if (reminder != null) {
                    cancelAlarm(context, reminder)
                    //cancela la notificacion para que no siga apareciendo
                    NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
                }
            }

            RECHAZAR -> { //REJECT SIGNIFICA RECHAZAR
                runBlocking {
                    //  updateUseCase.invoke(reminder.copy(isTaken = true))

                    if (reminder != null) {
                        // 1. Obtener el ID del GastosProgramadosModel
                        val gastosProgramadosId = reminder.gastosProgramadosId
                        // 2. Obtener el GastosProgramadosModel por su ID
                        val item = gastosProgramadosFirestore.get()
                            .firstOrNull { it.uid == gastosProgramadosId }
                        // reminder?.copy(isTaken = true)
                        if(item != null){
                            gastosProgramadosFirestore.update(item.copy(select = false))
                        }
                    }
                }
                if (reminder != null) {
                    cancelAlarm(context, reminder)
                    NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
                }
            }

            else -> {
                val smallIconId = reminder?.icon ?: R.drawable.ic_ahorro

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Se ejecuta si el dispositivo tiene Android 13 (Tiramisu) o una versión superior.
                    // A partir de Android 13, se requiere el permiso POST_NOTIFICATIONS para mostrar notificaciones.
                    if (ContextCompat.checkSelfPermission(
                            context,
                            POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // El usuario ha concedido el permiso POST_NOTIFICATIONS.
                        // Se puede mostrar la notificación.
                        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_NAME)
                            .setSmallIcon(reminder?.icon ?: R.drawable.ic_ahorro)
                            .setContentTitle(reminder?.title)
                            .setContentText(reminder?.message)
                            .setLargeIcon(BitmapFactory.decodeResource(context.resources, smallIconId))
                            .addAction(R.drawable.ic_add_task, "Hecho", donePendingIntent)
                            .addAction(R.drawable.ic_dark_mode, "Cerrar", closePendingIntent)
                            .build()
                        NotificationManagerCompat.from(context)
                            .notify(1, notification)
                    }else{
                        // El usuario NO ha concedido el permiso POST_NOTIFICATIONS.
                        // En este caso, no se muestra la notificación.
                        // Aquí podrías añadir código para solicitar el permiso al usuario.
                    }
                } else {
                    // Se ejecuta si el dispositivo tiene Android 12 o una versión inferior.
                    // En estas versiones, el permiso POST_NOTIFICATIONS no es necesario.
                    val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_NAME)
                        .setSmallIcon(reminder?.icon ?: R.drawable.ic_ahorro)
                        .setContentTitle(reminder?.title) // Título
                        .setContentText(String.format("debes pagar: ${reminder?.cashGastosprogramadosId}") )// Total a pagar (o texto principal)
                        .setSubText(reminder?.message) // Subtítulo
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, smallIconId))
                        .addAction(R.drawable.ic_add_task, "Hecho", donePendingIntent)
                        .addAction(R.drawable.ic_dark_mode, "Cerrar", closePendingIntent)
                        .build()

                    NotificationManagerCompat.from(context)
                        .notify(1, notification)

                }
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.release()
                }
                mediaPlayer.start()
            }
        }
    }
}