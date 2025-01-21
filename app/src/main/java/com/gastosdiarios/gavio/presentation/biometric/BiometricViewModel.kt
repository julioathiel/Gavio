package com.gastosdiarios.gavio.presentation.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.gastosdiarios.gavio.data.BiometricPromptManager
import dagger.hilt.android.lifecycle.HiltViewModel
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