package com.gastosdiarios.gavio.presentation

import android.util.Log
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedDataRepository @Inject constructor(
    private val userPref: UserPreferencesFirestore,
    private val userDataFirestore: UserDataFirestore
) {

    private val tag = "SharedDataRepository"

    private val _getData = MutableStateFlow<UserData?>(null)
    val getData: StateFlow<UserData?> = _getData.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() {
        CoroutineScope(Dispatchers.IO).launch { // Usa Dispatchers.IO para operaciones de red/BD
            try {
                val data = userDataFirestore.get()
                _getData.value = data
            } catch (e: Exception) {
                Log.e(tag, "Error en getUserData", e)
            }
        }
    }

    fun updateCurrentMoney(valueCurrentMoney: Double, isCurrentMoneyChecked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDataFirestore.updateCurrentMoney(valueCurrentMoney, isCurrentMoneyChecked)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateCurrentMoney", e)
            }
        }
    }

    fun updateTotalGastos(valueTotalGastos: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDataFirestore.updateTotalGastos(valueTotalGastos)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateTotalGastos", e)
            }
        }
    }

    fun updateTotalIngresos(valueTotalIngresos: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDataFirestore.updateTotalIngresos(valueTotalIngresos)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateTotalIngresos", e)
            }
        }
    }

    fun updateSelectedDate(date: String, isSelectedDate: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDataFirestore.updateSelectedDate(date, isSelectedDate)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateSelectedDate", e)
            }
        }
    }
}