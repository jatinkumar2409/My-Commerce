package com.example.mycommerce.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.mycommerce.domain.networkObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class networkObserverImpl(private val context : Context) : networkObserver {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun isConnected(): Flow<Boolean> = callbackFlow {
        val currentNetwork = connectivityManager.activeNetwork
        val currentCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
        val isConnected = currentCapabilities?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&  it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false
        trySend(isConnected)


       val networkCallback = object : ConnectivityManager.NetworkCallback(){
           override fun onAvailable(network: Network) {
               super.onAvailable(network)
               trySend(true)
           }
           override fun onCapabilitiesChanged(
               network: Network,
               networkCapabilities: NetworkCapabilities
           ) {
               super.onCapabilitiesChanged(network, networkCapabilities)
               val validated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
               val internet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
               if (validated && internet){
                   trySend(true)
               }
               else{
                   trySend(false)
               }
           }

           override fun onLost(network: Network) {
               super.onLost(network)
               trySend(false)
           }

           override fun onUnavailable() {
               super.onUnavailable()
               trySend(false)
           }
       }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}