package com.gastosdiarios.gavio.presentation.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.MainActivity
import com.gastosdiarios.gavio.data.BiometricPromptManager
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.ConfigurationUiState
import com.gastosdiarios.gavio.domain.model.SeguridadBiometrica
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BiometricViewModel @Inject constructor() : ViewModel() {

    private val biometricManager = BiometricPromptManager(AppCompatActivity())

    fun showBiometricPrompt(
        title: String,
        subtitle: String,
        activity: AppCompatActivity,
        auth: (isAuthorized: Boolean) -> Unit
    ) {
        biometricManager.showBiometric(activity,title, subtitle, auth)
    }
}