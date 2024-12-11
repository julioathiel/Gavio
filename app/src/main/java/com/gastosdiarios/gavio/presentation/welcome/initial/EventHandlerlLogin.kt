package com.gastosdiarios.gavio.presentation.welcome.initial

sealed class EventHandlerlLogin {
    data class ContinuarConPhone(val value:Boolean):EventHandlerlLogin()
    data class ContinuarConGoogle(val a: Unit) :EventHandlerlLogin()
    data class ContinuarConFacebok(val value:Boolean):EventHandlerlLogin()
}