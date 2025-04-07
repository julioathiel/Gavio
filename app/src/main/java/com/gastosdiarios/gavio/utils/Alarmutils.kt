package com.gastosdiarios.gavio.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gastosdiarios.gavio.data.domain.model.Alarm
import com.gastosdiarios.gavio.receiver.AlarmReceiver
import com.gastosdiarios.gavio.utils.Constants.ALARM_ID
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun setUpAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(ALARM_ID, Json.encodeToString(alarm))
    }

    val pendingIntent =
        PendingIntent.getBroadcast(
            context, alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    try {
       // alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

fun cancelAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(ALARM_ID, Json.encodeToString(alarm))
    }

    val pendingIntent =
        PendingIntent.getBroadcast(
            context, alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    try {
        alarmManager.cancel(pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

fun setUpPeriodicAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(ALARM_ID, Json.encodeToString(alarm))
    }

    val pendingIntent =
        PendingIntent.getBroadcast(
            context, alarm.id.toInt(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


    try {
        //  val interval = AlarmManager.INTERVAL_DAY
        val interval = 2L * 60L * 1000L
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            interval,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}