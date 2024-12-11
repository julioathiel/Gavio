package com.gastosdiarios.gavio.data.ui_state

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val photoUrl: String = "",
    val repeatPassword:String = "",
    val isLoginEnable:Boolean = false
)