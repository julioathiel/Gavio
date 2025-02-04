package com.gastosdiarios.gavio.data.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Alarm(
    var id:Long,
    val icon:Int,
    val title: String,
    val message: String,
    val timeInMillis:Long,
    val gastosProgramadosId: String = "",
    val cashGastosprogramadosId: String = "",
    val isTaken:Boolean = false,
    val isRepeat :Boolean = false
)