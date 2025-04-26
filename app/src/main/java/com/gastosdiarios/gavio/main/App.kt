package com.gastosdiarios.gavio.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.credentials.CredentialManager
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.utils.Constants.ALARM_CHANNEL_NAME
import com.gastosdiarios.gavio.utils.NetworkReceiver
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var credentialManager: CredentialManager
    private val networkReceiver by lazy { NetworkReceiver(this) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // Inicializa Firebase aquí
        credentialManager = CredentialManager.create(this)
        networkReceiver.register()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.reminder)
            val channelDescription = getString(R.string.reminder_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(ALARM_CHANNEL_NAME, name, importance)
            mChannel.description = channelDescription
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}