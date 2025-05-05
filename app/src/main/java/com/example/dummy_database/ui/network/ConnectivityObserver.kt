package com.example.dummy_database.ui.network

/**
 * Defines interfaces and concrete implementations for observing network connectivity status
 * using Android's ConnectivityManager. Provides a Compose utility function
 * to easily consume the connectivity state within composables.
 *
 * Responsibilities: Newton
 */


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState      // Compose helper to collect Flow as State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext    // Compose way to get the Android context
import kotlinx.coroutines.channels.awaitClose       // Flow builder utility for closing the channel
import kotlinx.coroutines.flow.Flow                 //Kotlin Flow for asynchronous data streams.
import kotlinx.coroutines.flow.callbackFlow         // Flow builder utility for emitting values
import androidx.compose.runtime.getValue            // Compose helper to read state values


// represents the connectivity status of the device
enum class ConnectivityStatus { Available, Unavailable }


// Interface defining a connectivity observer that emits status updates
interface ConnectivityObserver {
    // returns a Flow of ConnectivityStatus
    fun observe(): Flow<ConnectivityStatus>
}

/**
 * Concrete implementation of [ConnectivityObserver] using Android's
 * [ConnectivityManager] and network callbacks.
 *
 * @param context to access system connectivity services.
 */
class NetworkConnectivityObserver(context: Context) : ConnectivityObserver {
    // ConnectivityManager for registering network callbacks
    private val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Observes network changes and emits [ConnectivityStatus] updates via a [Flow]
     *
     * Uses a callbackFlow to bridge the ConnectivityManager callbacks into a coroutine Flow
     */
    override fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        // Helper function to check network status
        fun checkNetwork(n: Network?): ConnectivityStatus {
            val nc = n?.let { cm.getNetworkCapabilities(it) }
            return if (nc?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)
                ConnectivityStatus.Available
            else
                ConnectivityStatus.Unavailable
        }

        // Callback for network status changes
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityStatus.Available)
            }
            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        // Build a request for networks that have Internet
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Register the network callback with the ConnectivityManager
        cm.registerNetworkCallback(request, callback)
        // Emit the current status
        trySend(checkNetwork(cm.activeNetwork))

        // Unregister the callback when the flow is closed
        awaitClose {
            cm.unregisterNetworkCallback(callback)
        }
    }
}

// composable helper that returns the current connectivity status
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