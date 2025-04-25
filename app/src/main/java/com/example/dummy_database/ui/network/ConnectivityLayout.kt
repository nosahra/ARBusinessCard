package com.example.dummy_database.ui.network


import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Wrap your screen's content in this layout to get an automatic "Offline" banner.
 */
@Composable
fun ConnectivityLayout(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val observer = remember { NetworkConnectivityObserver(context) }
    // collectAsState(initial = Available) so we always have a value
    val status by observer.observe().collectAsState(initial = ConnectivityStatus.Available)
    val isOffline = status == ConnectivityStatus.Unavailable

    Column {
        if (isOffline) {
            OfflineBanner()
            Spacer(modifier = Modifier.height(8.dp))
        }
        content()
    }
}