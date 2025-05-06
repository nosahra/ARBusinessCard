package com.example.dummy_database.ui.scanner

/**
 * Defines the Scanner screen UI and logic.
 * This screen is responsible for launching the QR code scanner,
 * handling the scan result (a user ID), fetching corresponding data
 * from Firestore and launching the Augmented Reality (AR) experience
 * with the retrieved business card data. It also includes basic
 * network connectivity handling.
 *
 * Sole Contributor: Newton
 */


import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import com.example.dummy_database.ar.HelloArActivity
import com.example.dummy_database.ui.network.ConnectivityStatus
import com.example.dummy_database.ui.network.rememberConnectivityState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




@Composable
fun ScannerScreen(
    onBackClick: () -> Unit
) {
    // Obtains Android context for launching activities
    val context = LocalContext.current

    // state to hold the scanned user ID
    var scannedUid by remember { mutableStateOf<String?>(null) }

    // states to hold each field fetched from Firestore
    var introduction by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var linkedInLink by remember { mutableStateOf("") }
    var githubLink by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var voicePreference by remember { mutableStateOf("") }
    var avatarId by remember { mutableStateOf("") }


    // Firestore instance for retrieving data
    val db = Firebase.firestore

    // launcher that uses custom ZXing contract to scan QR codes
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ZxingScannerContract(),
        onResult = { result: String? ->
            if (result != null) {
                scannedUid = result //save the scanned user ID for debugging
                Log.d("ScannerScreen", "Scanned doc ID: $result")

                // Fetches the document with scanned ID from Firestore
                db.collection("cardholders")
                    .document(result)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {

                            //parse each field
                            introduction = document.getString("introduction") ?: ""
                            education = document.getString("education") ?: ""
                            experience = document.getString("experience") ?: ""
                            hobbies = document.getString("hobbies") ?: ""
                            linkedInLink = document.getString("linkedInUrl") ?: ""
                            githubLink = document.getString("githubUrl") ?: ""
                            emailAddress = document.getString("email") ?: ""
                            voicePreference = document.getString("voicePreference") ?: ""
                            avatarId = document.getString("avatarId") ?: ""

                            // prepare intent to launch AR experience
                            val intent = Intent(context, HelloArActivity::class.java).apply {
                                putExtra("education", education)
                                putExtra("experience", experience)
                                putExtra("hobbies", hobbies)
                                putExtra("introduction", introduction)
                                putExtra("linkedInUrl", linkedInLink)
                                putExtra("githubUrl", githubLink)
                                putExtra("email", emailAddress)
                                putExtra("voicePreference", voicePreference)
                                putExtra("avatarId", avatarId)

                            }
                            // start AR activity
                            context.startActivity(intent)
                            // Navigate back to Home screen
                            onBackClick()
                        } else {
                            Log.w("ScannerScreen", "No such document or doc doesn't exist")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ScannerScreen", "Error getting document", e)
                    }
            } else {
                Log.d("ScannerScreen", "Scan cancelled or null")
            }
        }
    )


    // observes ConnectivityStatus.Available/Unavailable
    val connectivityStatus = rememberConnectivityState()

    // Unavailable → Available => launch scanner
    LaunchedEffect(connectivityStatus) {
        if (connectivityStatus == ConnectivityStatus.Available) {
            scannerLauncher.launch(Unit)
        }
    }

    // UI: simple column showing retry button when offline
    // or a loading text when auto-launching camera
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (connectivityStatus == ConnectivityStatus.Unavailable) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = { scannerLauncher.launch(Unit) }) {
                Text("Retry")
            }
        } else {
            Text("Opening camera…")
        }
    }

}





