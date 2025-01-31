package com.gastosdiarios.gavio

import android.app.Application
import androidx.credentials.CredentialManager
import com.gastosdiarios.gavio.utils.NetworkReceiver
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var credentialManager: CredentialManager
    private val networkReceiver by lazy { NetworkReceiver(this) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // Inicializa Firebase aquí
        credentialManager = CredentialManager.create(this)
        networkReceiver.register()
    }
}