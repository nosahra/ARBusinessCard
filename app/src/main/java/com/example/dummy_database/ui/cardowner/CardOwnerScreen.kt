package com.example.dummy_database.ui.cardowner

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dummy_database.utils.generateQrCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await  // (Important) for .await()

@Composable
fun CardOwnerScreen(
    onBackClick: () -> Unit
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    // Remember the last interaction time
    val scope = rememberCoroutineScope()
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val timeoutMillis = 5 * 60 * 1000L // 5 minutes




    // We’ll store each text field in local state.
    var linkedInUrl by remember { mutableStateOf("") }
    var introduction by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }

    // Radio Button state and options
    var selectedAvatar by remember { mutableStateOf<String?>(null) }
    val avatarOptions = listOf("avatar_man", "avatar_woman")

    var voicePreference by remember {mutableStateOf<String?>(null) }
    val avatarVoices = listOf("MALE", "FEMALE")

    // The user's UID (document ID)
    val uid = auth.currentUser?.uid ?: ""

    // We'll store  uId in local state (could just use uid directly)
    var  uId by remember { mutableStateOf("") }

    // For scrolling if fields get long
    val scrollState = rememberScrollState()

    // Alert Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    // Track if we can save (based on whether an avatar is selected)
    val canSaveAvatar = selectedAvatar != null
    val canSaveVoice = voicePreference != null

    // When the screen first appears, load existing data
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            try {
                val docSnap = db.collection("cardholders").document(uid).get().await()
                if (docSnap.exists()) {
                    linkedInUrl = docSnap.getString("linkedInUrl") ?: ""
                    introduction = docSnap.getString("introduction") ?: ""
                    education = docSnap.getString("education") ?: ""
                    experience = docSnap.getString("experience") ?: ""
                    hobbies = docSnap.getString("hobbies") ?: ""
                    githubUrl = docSnap.getString("githubUrl") ?: ""
                    gmail = docSnap.getString("gmail") ?: ""
                    voicePreference = docSnap.getString("voicePreference")
                    selectedAvatar = docSnap.getString("avatarId")
                }
                // We'll set  uId to the user's UID for the QR code
                 uId = uid
            } catch (e: Exception) {
                Log.w("CardOwnerScreen", "Error fetching existing doc: ", e)
            }
        }
    }

    // ----------------------
    // (4) Inactivity Timer Loop
    // ----------------------
    // start a background job that checks inactivity every second.
    // If inactivity > 5 min, sign out & navigate away
    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                delay(1000) // check every second
                val now = System.currentTimeMillis()
                val inactiveTime = now - lastInteractionTime

                if (inactiveTime > timeoutMillis && auth.currentUser != null) {
                    // Log out user
                    auth.signOut()
                    // navigate away
                    onBackClick()
                    break // stop the loop
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                // any activity or touch will reset the timer
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                lastInteractionTime = System.currentTimeMillis()
            }
        }
    },
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CardOwner Screen")

        // LinkedIn URL field
        OutlinedTextField(
            value = linkedInUrl,
            onValueChange = {
                linkedInUrl = it
                lastInteractionTime = System.currentTimeMillis() //resets on typing
                            },
            label = { Text("LinkedIn URL") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Fetch Button (Just a placeholder for future LinkedIn API logic)
        Button(
            onClick = {
                introduction = "Hi, I’m John, a business strategist helping companies scale and thrive. Let’s discuss how I can drive growth for your organization. Let’s connect!"
                education = "I completed my BSc in Business Administration from University of Toronto."
                experience = "I worked as a business strategist for 2 years at IBM."
                hobbies = "I love playing football and learning new things."
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Fetch from LinkedIn")
        }

        // Introduction field
        OutlinedTextField(
            value = introduction,
            onValueChange = { introduction = it },
            label = { Text("Introduction") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Education field
        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            label = { Text("Education") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Experience field
        OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("Experience") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Hobbies field
        OutlinedTextField(
            value = hobbies,
            onValueChange = { hobbies = it },
            label = { Text("Hobbies") },
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // github field
        OutlinedTextField(
            value = githubUrl,
            onValueChange = { githubUrl = it },
            label = { Text("GitHub URL") },
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // gmail field
        OutlinedTextField(
            value = gmail,
            onValueChange = { gmail = it },
            label = { Text("Gmail") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Voice Preference field
        Text("Select Voice Preference:")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            avatarVoices.forEach { avatarVoice ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = voicePreference == avatarVoice,
                        onClick = { voicePreference = avatarVoice }
                    )
                    Text(text = avatarVoice)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }


        // Radio Button Group for Avatar Selection
        Text("Select Avatar:")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            avatarOptions.forEach { avatar ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAvatar == avatar,
                        onClick = { selectedAvatar = avatar }
                    )
                    Text(text = avatar)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Save Button -> Overwrite the doc at /cardholders/<uid>
        Button(
            onClick = {
//                if (uid.isEmpty()) {
//                    Log.w("CardOwnerScreen", "No user logged in. Cannot save.")
//                    return@Button
//                }
                if (!canSaveAvatar) {
                    // Show Alert Dialog:
                    alertMessage = "Please select an avatar before saving."
                    showDialog = true
                }
                else if (!canSaveVoice) {
                    // Show Alert Dialog:
                    alertMessage = "Please select a voice before saving."
                    showDialog = true
                }
                else{

                val data = mapOf(
                    "linkedInUrl" to linkedInUrl,
                    "introduction" to introduction,
                    "education" to education,
                    "experience" to experience,
                    "hobbies" to hobbies,
                    "githubUrl" to githubUrl,
                    "gmail" to gmail,
                    "voicePreference" to voicePreference,
                    "avatarId" to selectedAvatar
                )
                db.collection("cardholders")
                    .document(uid)
                    .set(data)  // Overwrite or create the doc
                    .addOnSuccessListener {
                        uId = uid  // We know the doc ID is just the UID
                        Log.d("CardOwnerScreen", "Data saved for user $uid")
                    }
                    .addOnFailureListener { e ->
                        Log.w("CardOwnerScreen", "Error saving data", e)
                    }

                // Reset lastInteractionTime
                lastInteractionTime = System.currentTimeMillis()

            }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save")
        }

        // If we have a  uId, show the QR code
        if ( uId.isNotEmpty()) {
            //Text("QR Code for your ID: $ uId")
            QrCodeImage( uId =  uId)
        }

//        // Optionally show a back button
//        Button(
//            onClick = onBackClick,
//            modifier = Modifier.padding(top = 16.dp)
//        ) {
//            Text("Back")
//        }

        // If user is logged in, show a Logout button
        if (auth.currentUser != null) {
            Button(
                onClick = {
                    auth.signOut()
                    onBackClick()  // go back or navigate somewhere else
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Logout")
            }
        }
    }
}

data class CardholderData(
    val linkedInUrl: String = "",
    val githubUrl: String = "",
    val gmail: String = "",
    val introduction: String = "",
    val education: String = "",
    val experience: String = "",
    val hobbies: String = "",
    val avatarId: String = "",
    val voicePreference: String = ""
)

@Composable
fun QrCodeImage( uId: String) {
    val bitmap = generateQrCode( uId)
    if (bitmap != null) {
        Image(bitmap.asImageBitmap(), contentDescription = "QR Code")
    } else {
        Text("Failed to generate QR code")
    }
}

@Preview(showBackground = true)
@Composable
fun CardOwnerScreenPreview() {
    CardOwnerScreen(onBackClick = {})
}
