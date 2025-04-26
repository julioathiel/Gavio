package com.gastosdiarios.gavio.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkReceiver(private val context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // La red está disponible
            // Verificar si hay una conexión a internet activa
//            MainActivity.ConnectivityStatus.isConnected.value = true
            if (IsInternetAvailableUtils.isInternetAvailable(context)) {
               // App.ConnectivityStatus.isConnected.value = true
            }
        }

        override fun onLost(network: Network) {
            // La red se ha perdido
            if (!IsInternetAvailableUtils.isInternetAvailable(context)) {
            //    App.ConnectivityStatus.isConnected.value = false
            }
        }
    }

    fun register() {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}