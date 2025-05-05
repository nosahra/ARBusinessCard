package com.example.dummy_database.ui.network

/**
 * A composable layout wrapper that observes network connectivity and displays
 * an "Offline" banner at the top of its content when the network is unavailable.
 *
 * Responsibilities:  Newton
 */

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Wrap a screen's content in this layout to get an automatic "Offline" banner.
 */
@Composable
fun ConnectivityLayout(
    content: @Composable () -> Unit
) {
    // get the Android Context for initializing connectivity observer
    val context = LocalContext.current
    // create the connectivity observer
    val observer = remember { NetworkConnectivityObserver(context) }
    // collectAsState(initial = Available) so we always have a value(avoiding null state)
    val status by observer.observe().collectAsState(initial = ConnectivityStatus.Available)
    // determine whether to show banner
    val isOffline = status == ConnectivityStatus.Unavailable

    Column {
        // when offline show the banner before the content
        if (isOffline) {
            OfflineBanner()
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Render the wrapped screen content
        content()
    }
}