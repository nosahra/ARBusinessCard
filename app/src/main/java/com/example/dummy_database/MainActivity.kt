// MainActivity.kt
package com.example.dummy_database

/**
 * Main entry point of our application, setting up the Compose UI,
 * navigation graph, and initial destination logic based on user preferences.
 * It also wraps the main content with a network connectivity status layout.
 *
 * Contributions=>
 * Newton: setting up the Compose UI, navigation graph and wrapper for offline banner
 * Sahra: initial destination logic for user's first launch to show help screen
 */


import android.content.Context      //for accessing SharedPreferences
import android.os.Bundle            //lifecycle bundle for Activity state
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


/**
 * The main activity class that serves as the host for the Compose UI.
 * It sets up the initial content view of the application.
 */
class MainActivity : ComponentActivity() {
    // Called when the activity is first created. Set up the Compose UI content
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Set the theme for the app
            Dummy_DatabaseTheme {
                MyApp()
            }
        }
    }
}

/**
 * MyApp is the root composable that handles:
 * 1. Checking if this is the user's first launch (to show help).
 * 2. Showing a loading spinner while determination is in progress.
 * 3. Wrapping all screens in a connectivity-aware layout.
 * 4. Defining the navigation graph for all app destinations.
 */
@Composable
fun MyApp() {
    val navController = rememberNavController()     // Navigation controller to manage app navigation state
    val context = LocalContext.current              // Access the app's context
    val isFirstTime = remember { mutableStateOf<Boolean?>(null) }  // State to track if it's the user's first launch

    // Check SharedPreferences once
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasSeenHelp = prefs.getBoolean("hasSeenHelp", false)

        // check if the help screen has been seen or, not
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
        // Decide the initial destination based on first-time flag
        val startDest = if (isFirstTime.value == true) HELP_ROUTE else HOME_ROUTE

        // Apply theme and surface
        Surface(color = MaterialTheme.colorScheme.background) {
            // Wrap all screens in a connectivity-aware layout to show offline banner when disconnected
            ConnectivityLayout {

                // defines the navigation graph for all app destinations
                NavHost(
                    navController = navController,
                    startDestination = startDest
                ) {
                    // Home screen route
                    composable(HOME_ROUTE) {
                        HomeScreen(
                            onCardOwnerScreenClick = { navController.navigate(CARDOWNER_ROUTE) },
                            onScannerScreenClick = { navController.navigate(SCANNER_ROUTE) },
                            onNeedAuth = { navController.navigate(AUTH_ROUTE) },
                            onHelp = { navController.navigate(HELP_ROUTE) }
                        )
                    }

                    // Card owner screen route
                    composable(CARDOWNER_ROUTE) {
                        CardOwnerScreen(onBackClick = { navController.navigateUp() })
                    }

                    //Scanner screen route
                    composable(SCANNER_ROUTE) {
                        ScannerScreen(onBackClick = { navController.navigateUp() })
                    }

                    // Authentication screen route
                    composable(AUTH_ROUTE) {
                        AuthScreen(
                            onAuthSuccess = {
                                // Navigate to CardOwnerScreen after successful auth
                                navController.navigate(CARDOWNER_ROUTE) {
                                    popUpTo(AUTH_ROUTE) { inclusive = true }
                                }
                            },
                            onBackClick = { navController.navigateUp() }
                        )
                    }

                    // Help screen route: shown only on first launch
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



