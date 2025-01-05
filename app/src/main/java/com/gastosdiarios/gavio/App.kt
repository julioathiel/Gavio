package com.gastosdiarios.gavio

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import com.gastosdiarios.gavio.utils.NetworkReceiver
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    companion object ConnectivityStatus {
        var isConnected = mutableStateOf(true)
       lateinit var auth: FirebaseAuth
       lateinit var credentialManager: CredentialManager
        lateinit var networkReceiver: NetworkReceiver
   }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // Inicializa Firebase aquí
        networkReceiver = NetworkReceiver(this)
        networkReceiver.register()
        credentialManager = CredentialManager.create(this)
        auth = firebaseAuth
    }
}