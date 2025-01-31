package com.gastosdiarios.gavio.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gastosdiarios.gavio.MainActivity
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.constants.Constants.MY_CHANNEL_ID
import com.gastosdiarios.gavio.data.constants.Constants.NOTIFICATION_ID
import com.gastosdiarios.gavio.domain.model.NotificationProgrammedModel

class NotificationProgrammed : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("alarma", "¡Alarma disparada!")

        val notification = NotificationProgrammedModel(
            id = NOTIFICATION_ID,
            smallIcon = R.drawable.ic_ahorro,
            title = context.getString(R.string.title_notification_programada),
            text = context.getString(R.string.textLarge_notification_programada),
            largeIconResId = R.drawable.ic_ahorro,
            priority = NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotification(context, notification)
    }


    private fun createNotification(context: Context, notification: NotificationProgrammedModel) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        //esta parte sirve para abrir la app al presionar la notification
        val intent = Intent(context, MainActivity::class.java).apply {
            //crea una nueva bandera y elimina otra existente antes de iniciar la nueva
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Usa IMMUTABLE si no necesitas modificar el intent
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, flag)

        val notifications = NotificationCompat.Builder(context, MY_CHANNEL_ID)
            .setSmallIcon(notification.smallIcon)
            .setContentTitle(notification.title)
            .setContentText(notification.text)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    notification.largeIconResId
                )
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(notification.priority)
            .build()

        notificationManager.notify(notification.id, notifications)
    }
}