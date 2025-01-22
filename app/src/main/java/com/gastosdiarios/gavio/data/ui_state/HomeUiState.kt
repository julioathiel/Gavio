package com.gastosdiarios.gavio.data.ui_state

import android.net.Uri

data class HomeUiState(
    val selectedImageUri: Uri? = null,
    val dineroActual: Double? = null,
    val mostrandoDineroTotalIngresos: Double? = null,
    val mostrandoDineroTotalGastos: Double? = null,
    val cantidadIngresada: String? = "",
    val categoryName: String? = "",
    val description: String? = "",
    val fechaElegida: String? = "",
    val limitMonth: Int = 0,
    val buttonIngresosActivated: Int = 0,
    val diasRestantes:Int = 0,
    val limitePorDia:Double = 0.0,
    val enabledButtonGastos: Boolean = false,
    val showTransaction:Boolean = false,
    val isChecked: Boolean = false,
    val agregar: Boolean = false,
    val editar: Boolean = false,
    val showNuevoMes: Boolean = false,
    val isLoading: Boolean = false,
    val currentUser: String? = null,
    val isError: Boolean = false
)