package com.example.dummy_database.ui.scanner


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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material3.AlertDialog

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
    val context = LocalContext.current

    // We'll store the scannedUid after scanning
    var scannedUid by remember { mutableStateOf<String?>(null) }

    // We'll also store the fetched data
    var introduction by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var linkedInLink by remember { mutableStateOf("") }
    var githubLink by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var voicePreference by remember { mutableStateOf("") }
    var avatarId by remember { mutableStateOf("") }


    // Firestore reference
    val db = Firebase.firestore

    // The launcher that starts the ZXing scanner
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ZxingScannerContract(),
        onResult = { result: String? ->
            if (result != null) {
                scannedUid = result
                Log.d("ScannerScreen", "Scanned doc ID: $result")

                // 1) Fetch from Firestore
                db.collection("cardholders")
                    .document(result)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {

                            // We parse each field. Adjust to match your schema
                            introduction = document.getString("introduction") ?: ""
                            education = document.getString("education") ?: ""
                            experience = document.getString("experience") ?: ""
                            hobbies = document.getString("hobbies") ?: ""
                            linkedInLink = document.getString("linkedInUrl") ?: ""
                            githubLink = document.getString("githubUrl") ?: ""
                            emailAddress = document.getString("email") ?: ""
                            voicePreference = document.getString("voicePreference") ?: ""
                            avatarId = document.getString("avatarId") ?: ""

                            // Launch the AR activity directly.
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
                            // Optionally, you can pass extras if needed:
                            // intent.putExtra("avatar_id", "default")
                            context.startActivity(intent)
                            // Navigate back to the Home screen
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


    // now yields ConnectivityStatus.Available or .Unavailable
    val connectivityStatus = rememberConnectivityState()

    // Launch the QR scanner as soon as we enter this screen
//    LaunchedEffect(Unit) {
//        scannerLauncher.launch(Unit)
//    }

    // only fire the camera‐launch effect when we go from Unavailable → Available
    LaunchedEffect(connectivityStatus) {
        if (connectivityStatus == ConnectivityStatus.Available) {
            scannerLauncher.launch(Unit)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (connectivityStatus == ConnectivityStatus.Unavailable) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = { scannerLauncher.launch(Unit) }) {
                Text("Retry scan")
            }
        } else {
            Text("Opening camera…")
        }
    }


//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Employer Screen")
//
//        // Button to launch the QR scanner
//        Button(
//            onClick = { scannerLauncher.launch(Unit) },
//            modifier = Modifier.padding(top = 16.dp)
//        ) {
//            Text("Scan QR Code")
//        }

//        // If we have a scannedUid, show the data
//        scannedUid?.let { uid ->
//            Text("Scanned User ID: $uid")
//
//            // For now, just display the fields
//            Text("Introduction: $introduction")
//            Text("Education: $education")
//            Text("Experience: $experience")
//            Text("Hobbies: $hobbies")
//        }

//        //
//        Text("Opening camera ...", modifier = Modifier.padding(top = 16.dp))
//
//        // Back button
//        Button(
//            onClick = onBackClick,
//            modifier = Modifier.padding(top = 16.dp)
//        ) {
//            Text("Back")
//        }
//    }
}





