package com.example.dummy_database.ui.navigation

/**
 * Defines all navigation route constants used in the NavHost graph.
 *
 * Each constant here corresponds to a composable destination in the app.
 * Using centralized route names prevents typos and makes refactoring easier.
 *
 * Responsibiliites:
 * Sahra: Added help route
 * Newton: Added rest of the routes
 */




object AppDestinations {
    const val AUTH_ROUTE = "auth"
    const val HOME_ROUTE = "home"
    const val CARDOWNER_ROUTE = "cardholder"
    const val SCANNER_ROUTE = "employer"
    const val HELP_ROUTE = "help"
}