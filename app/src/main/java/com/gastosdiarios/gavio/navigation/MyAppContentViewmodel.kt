package com.gastosdiarios.gavio.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.utils.IsInternetAvailableUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAppContentViewmodel @Inject constructor(
    @ApplicationContext private val context: Context) : ViewModel() {

    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkInternetConnection(context) //Comprobación inicial
    }

    fun checkInternetConnection(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _isInternetAvailable.value = IsInternetAvailableUtils.isInternetAvailable(context)
            _isLoading.value = false
        }
    }
}