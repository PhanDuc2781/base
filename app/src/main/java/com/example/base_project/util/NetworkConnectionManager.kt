package com.example.base_project.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.base_project.applicattion.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class NetworkConnectionManager private constructor() {

    companion object {
        val shared = NetworkConnectionManager()
    }

    var isAvailable = false

    val networkStateChange = MutableStateFlow(isAvailable)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isAvailable = true
            CoroutineScope(Dispatchers.Default).launch { networkStateChange.emit(isAvailable) }
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            isAvailable = false
            CoroutineScope(Dispatchers.Default).launch { networkStateChange.emit(isAvailable) }
        }
    }

    private val networkRequest: NetworkRequest by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    fun registerForNetworkUpdates(application: MainApplication) {
        val connectivityManager =
            application.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}

fun Context.isInternetAvailable(): Boolean {
    return NetworkConnectionManager.shared.isAvailable
}