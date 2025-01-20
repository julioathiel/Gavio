package com.gastosdiarios.gavio.domain.model.modelFirebase

data class UserData(
    val userId: String? = null,
    val totalGastos: Double? = null,
    val totalIngresos: Double? = null,
    val currentMoney: Double? = null,
    @field:JvmField
    val isCurrentMoneyIngresos: Boolean? = null,//si es dinero ingresado o un gasto
    val selectedDate: String? = null,
    @field:JvmField
    val isSelectedDate: Boolean? = null
)
