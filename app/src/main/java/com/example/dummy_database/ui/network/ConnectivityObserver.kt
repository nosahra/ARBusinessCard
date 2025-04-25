package com.example.dummy_database.ui.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import androidx.compose.runtime.getValue


enum class ConnectivityStatus { Available, Unavailable }



interface ConnectivityObserver {
    /** Emits current status and thereafter on any change. */
    fun observe(): Flow<ConnectivityStatus>
}

class NetworkConnectivityObserver(context: Context) : ConnectivityObserver {
    private val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        // Helper to check if a network has INTERNET capability
        fun checkNetwork(n: Network?): ConnectivityStatus {
            val nc = n?.let { cm.getNetworkCapabilities(it) }
            return if (nc?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)
                ConnectivityStatus.Available
            else
                ConnectivityStatus.Unavailable
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityStatus.Available)
            }
            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        // Register to listen to all networks that have INTERNET
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, callback)
        // Emit initial state
        trySend(checkNetwork(cm.activeNetwork))

        awaitClose {
            cm.unregisterNetworkCallback(callback)
        }
    }
}

@Composable
fun rememberConnectivityState(): ConnectivityStatus {
    // 1) pull the Android Context
    val context = LocalContext.current
    // 2) only create the observer once
    val observer = remember { NetworkConnectivityObserver(context) }
    // 3) collect the Flow into a State<ConnectivityStatus>
    val status by observer.observe().collectAsState(initial = ConnectivityStatus.Available)
    return status
}