package com.gastosdiarios.gavio.data.repository

import com.gastosdiarios.gavio.data.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun insertAlarm(alarm: Alarm)

    suspend fun upsertAlarm(alarm: Alarm)

    fun getAlarmById(alarmId: Long): Alarm

    fun getAllAlarm(): Flow<List<Alarm>>

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun deleteAlarmById(alarmId: Long)

}