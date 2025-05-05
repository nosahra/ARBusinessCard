// MainActivity.kt
package com.example.dummy_database

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dummy_database.ui.auth.AuthScreen
import com.example.dummy_database.ui.navigation.AppDestinations.CARDOWNER_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.SCANNER_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.HOME_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.AUTH_ROUTE
import com.example.dummy_database.ui.cardowner.CardOwnerScreen
import com.example.dummy_database.ui.help.HelpScreen
import com.example.dummy_database.ui.scanner.ScannerScreen
import com.example.dummy_database.ui.home.HomeScreen
import com.example.dummy_database.ui.navigation.AppDestinations.HELP_ROUTE
import com.example.dummy_database.ui.network.ConnectivityLayout
import com.example.dummy_database.ui.theme.Dummy_DatabaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dummy_DatabaseTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val isFirstTime = remember { mutableStateOf<Boolean?>(null) }

    // Check SharedPreferences once
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasSeenHelp = prefs.getBoolean("hasSeenHelp", false)

        if (!hasSeenHelp) {
            prefs.edit().putBoolean("hasSeenHelp", true).apply()
            isFirstTime.value = true
        } else {
            isFirstTime.value = false
        }
    }

    if (isFirstTime.value == null) {
        // Show loading while determining destination
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDest = if (isFirstTime.value == true) HELP_ROUTE else HOME_ROUTE

        Surface(color = MaterialTheme.colorScheme.background) {
            ConnectivityLayout {
                NavHost(
                    navController = navController,
                    startDestination = startDest
                ) {
                    composable(HOME_ROUTE) {
                        HomeScreen(
                            onCardOwnerScreenClick = { navController.navigate(CARDOWNER_ROUTE) },
                            onScannerScreenClick = { navController.navigate(SCANNER_ROUTE) },
                            onNeedAuth = { navController.navigate(AUTH_ROUTE) },
                            onHelp = { navController.navigate(HELP_ROUTE) }
                        )
                    }
                    composable(CARDOWNER_ROUTE) {
                        CardOwnerScreen(onBackClick = { navController.navigateUp() })
                    }
                    composable(SCANNER_ROUTE) {
                        ScannerScreen(onBackClick = { navController.navigateUp() })
                    }
                    composable(AUTH_ROUTE) {
                        AuthScreen(
                            onAuthSuccess = {
                                navController.navigate(CARDOWNER_ROUTE) {
                                    popUpTo(AUTH_ROUTE) { inclusive = true }
                                }
                            },
                            onBackClick = { navController.navigateUp() }
                        )
                    }
                    composable(HELP_ROUTE) {
                        HelpScreen(
                            onNavigateHome = {
                                navController.navigate(HOME_ROUTE) {
                                    popUpTo(0) { inclusive = true } // Clear backstack
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}



