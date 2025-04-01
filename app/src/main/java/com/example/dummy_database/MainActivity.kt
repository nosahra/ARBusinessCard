// MainActivity.kt
package com.example.dummy_database

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dummy_database.ui.auth.AuthScreen
import com.example.dummy_database.ui.navigation.AppDestinations.CARDOWNER_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.SCANNER_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.HOME_ROUTE
import com.example.dummy_database.ui.navigation.AppDestinations.AUTH_ROUTE
import com.example.dummy_database.ui.cardowner.CardOwnerScreen
import com.example.dummy_database.ui.scanner.ScannerScreen
import com.example.dummy_database.ui.home.HomeScreen
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

    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = navController,
            startDestination = HOME_ROUTE
        ) {
            composable(HOME_ROUTE) {
                HomeScreen(
                    onCardOwnerScreenClick = { navController.navigate(CARDOWNER_ROUTE) },
                    onScannerScreenClick = { navController.navigate(SCANNER_ROUTE) },
                    onNeedAuth = { navController.navigate(AUTH_ROUTE) }
                )
            }
            composable(CARDOWNER_ROUTE) {
                CardOwnerScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
            composable(SCANNER_ROUTE) {
                ScannerScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(AUTH_ROUTE) {
                AuthScreen(
                    onAuthSuccess = {
                        // Once the user logs in, go back to Home
                        navController.navigate(CARDOWNER_ROUTE) {
                            // remove Auth screen from back stack
                            popUpTo(AUTH_ROUTE) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}


