package com.ghost.newsapp.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkConnectivityObserver(context: Context) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network became available -> emit true
                trySend(true)
            }
            override fun onLosing(network: Network, maxMsToLive: Int) {
                // Network is about to be lost -> emit false
                trySend(false)
            }
            override fun onLost(network: Network) {
                // Network was lost -> emit false
                trySend(false)
            }
            override fun onUnavailable() {
                // Requested network is unavailable -> emit false
                trySend(false)
            }
        }
        // Build a network request to listen for any Internet-capable network
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        // Register the callback with the ConnectivityManager
        connectivityManager.registerNetworkCallback(request, callback)
        // Ensure the callback is unregistered when the flow collection ends
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}