package com.example.dummy_database.ui.cardowner


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dummy_database.ui.network.ConnectivityStatus
import com.example.dummy_database.ui.network.rememberConnectivityState
import com.example.dummy_database.utils.generateQrCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await  // (Important) for .await()
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


fun isValidHost(rawUrl: String, requiredHost: String): Boolean {
    return try {
        // If the user forgot "http", add it so Uri.parse().host works:
        val normalized = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            rawUrl
        } else {
            "https://$rawUrl"
        }
        val host = Uri.parse(normalized).host?.lowercase() ?: return false
        host == requiredHost || host.endsWith(".$requiredHost")
    } catch (_: Exception) {
        false
    }
}



@Composable
fun CardOwnerScreen(
    onBackClick: () -> Unit
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    // now yields ConnectivityStatus.Available or .Unavailable
    val connectivityStatus = rememberConnectivityState()


    // store each text field in local state.
    var linkedInUrl by remember { mutableStateOf("") }
    var introduction by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }

    var linkedInError by remember { mutableStateOf<String?>(null) }
    var linkedInFetchError by remember { mutableStateOf<String?>(null) }
    var githubError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }


    // Radio Button state and options
    var selectedAvatar by remember { mutableStateOf<String?>(null) }
    val avatarOptions = listOf("avatar_man", "avatar_woman")

    var voicePreference by remember {mutableStateOf<String?>(null) }
    val avatarVoices = listOf("MALE", "FEMALE")

    // The user's UID (document ID)
    val uid = auth.currentUser?.uid ?: ""

    // store  uId in local state (could just use uid directly)
    var  uId by remember { mutableStateOf("") }

    // For scrolling if fields get long
    val scrollState = rememberScrollState()

    // Alert Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    // Track if we can save (based on whether an avatar is selected)
    val canSaveAvatar = selectedAvatar != null
    val canSaveVoice = voicePreference != null

    // A second dialog flag for logout confirmation
    var showLogoutConfirm by remember { mutableStateOf(false) }

    var showSaveSuccess by remember { mutableStateOf(false) }

    var introductionError by remember { mutableStateOf<String?>(null) }
    // A one‑line summary of why the save failed
    var errorMessage by remember { mutableStateOf("") }

    // Only show QR (and download) when true
    var qrVisible by remember { mutableStateOf(false) }

    // Intercept ANY back‑press and show logout dialog
    BackHandler {
//        auth.signOut()
//        onBackClick()
        showLogoutConfirm = true
    }

//    fun isValidHost(url: String, requiredHost: String): Boolean {
//        return try {
//            val h = url.toUri().host ?: return false
//            url.startsWith("http") && h.endsWith(requiredHost)
//        } catch (_: Exception) { false }
//    }

    /**
     * Returns true if [url] points at a host that is exactly
     * `requiredHost` or ends in `.` + `requiredHost`.
     * Automatically adds “https://” if no scheme is present.
     */



    // When the screen first appears, load existing data
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            try {
                val docSnap = db.collection("cardholders").document(uid).get().await()
                if (docSnap.exists()) {
                    qrVisible = true
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
                // set  uId to the user's UID for the QR code
//                 uId = uid
                // only set uId when there's data
                uId = if(docSnap.exists()) uid else ""
            } catch (e: Exception) {
                Log.w("CardOwnerScreen", "Error fetching existing doc: ", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Oops!") },
                text = { Text(alertMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // logout‑confirmation dialog
        if (showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = { Text("Confirm logout") },
                text = { Text("Have you saved your changes?") },
                confirmButton = {
                    Button(onClick = {
                        // actually sign out & navigate
                        FirebaseAuth.getInstance().signOut()
                        onBackClick()
                    }) {
                        Text("Yes, Logout")
                    }
                },
                dismissButton = {
                    Button(onClick = { showLogoutConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showSaveSuccess) {
            AlertDialog(
                onDismissRequest = { showSaveSuccess = false },
                title        = { Text("Saved!") },
                text         = { Text("Your changes have been saved.") },
                confirmButton = {
                    Button(onClick = { showSaveSuccess = false }) {
                        Text("OK")
                    }
                }
            )
        }


        Text("CardOwner Screen")

        // LinkedIn URL field
        OutlinedTextField(
            value = linkedInUrl,
            onValueChange = {
                linkedInUrl = it
                linkedInError = null    // clear error as soon as user starts typing
                            },
            label = { Text("LinkedIn URL") },
            isError = linkedInError != null,
                           supportingText = {
                                 linkedInError?.let { err ->
                                      Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                      }
                                            },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Fetch Button (Just a placeholder for future LinkedIn API logic). Pretends to fetch data.
        Button(
            onClick = {
                // clear any prior errors
                linkedInError = null
                linkedInFetchError = null

                // validate url
                if (linkedInUrl.isBlank()) {
                    // 1) URL empty → show required‐field error
                    linkedInFetchError = "Please enter your LinkedIn URL."
                }
                else if (!isValidHost(linkedInUrl, "linkedin.com")) {
                    linkedInError = "Must be a valid linkedin.com URL"
                }
                else {
                    // 2) pretend‐fetch → fill the dummies
                    introduction = "Hi, I’m John, a business strategist helping companies scale and thrive. Let’s discuss how I can drive growth for your organization. Let’s connect!"
                    education     = "I completed my BSc in Business Administration from University of Toronto."
                    experience    = "I worked as a business strategist for 2 years at IBM."
                    hobbies       = "I love playing football and learning new things."
                    // 3) then show the API‐not‐available message
                    linkedInFetchError = "LinkedIn API isn’t available. Below fields are filled with placeholder data. Please, update the placeholder data."
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Fetch from LinkedIn")
        }


        linkedInFetchError?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
        }

        // Introduction field
//        OutlinedTextField(
//            value = introduction,
//            onValueChange = { introduction = it },
//            label = { Text("Introduction") },
//            modifier = Modifier.padding(top = 16.dp)
//        )

        OutlinedTextField(
            value = introduction,
            onValueChange = {
                introduction = it
                introductionError = null    // clear error as soon as user starts typing
//                errorMessage="Saving failed! Introduction must not be empty."
                errorMessage=""
            },
            label     = { Text("Introduction") },
            isError   = introductionError != null,
            supportingText = {
                introductionError?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
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
            onValueChange = {
                          githubUrl = it
                          githubError = null
                        },
                    label = { Text("GitHub URL") },
                    isError = githubError != null,
                    supportingText = {
                          githubError?.let { err ->
                                Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                              }
                                     },
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // gmail field
        OutlinedTextField(
            value = gmail,
            onValueChange = {
                  gmail = it
                  emailError = null
                },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {
                  emailError?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                     }
               },
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
                        onClick = {
                            voicePreference = avatarVoice
//                            errorMessage="Saving failed! Please, select a voice preference."
                        }
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
                        onClick = {
                            selectedAvatar = avatar
//                            errorMessage="Saving failed! Please, select an avatar."
                        }
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
                // 1) reset any prior error
                introductionError = null
                errorMessage = ""
                linkedInError = null
                githubError = null
                emailError = null

                if(connectivityStatus == ConnectivityStatus.Unavailable){
                    errorMessage = "Saving failed! Please go online and try again."
                    return@Button
                }

                // 2) validate introduction
                if (introduction.isBlank()) {
                    introductionError = "Please complete your introduction for your AR business card."
                    errorMessage="Saving failed! Introduction must not be empty."
                    return@Button
                }

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

                  // 3) validate links


                  var linkOk = true
                  if (linkedInUrl.isNotBlank() && !isValidHost(linkedInUrl, "linkedin.com")) {
                        linkedInError = "Must be a valid linkedin.com URL"
                        linkOk = false
                      }
                  if (githubUrl.isNotBlank() && !isValidHost(githubUrl, "github.com")) {
                        githubError = "Must be a valid github.com URL"
                        linkOk = false
                      }
                  if (gmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(gmail).matches()) {
                        emailError = "Must be a valid email address"
                        linkOk = false
                      }
                  if (!linkOk) {
                        errorMessage = "Saving failed! Please fix the highlighted links."
                        return@Button
                      }

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
                        showSaveSuccess = true
                        errorMessage = ""
                        qrVisible = true
                    }
                    .addOnFailureListener { e ->
                        Log.w("CardOwnerScreen", "Error saving data", e)
                        errorMessage = when (e) {
                            is com.google.firebase.FirebaseNetworkException ->
                                "Saving failed! Please go online and try again."
                            else ->
                                "Saving failed: ${e.localizedMessage ?: "Unknown error"}"
                        }
                    }


            }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }


        // If we have a  uId, show the QR code
//        if ( qrVisible) {
//            //Text("QR Code for your ID: $ uId")
//            QrCodeImage( uId =  uId)
//        }

        if(qrVisible){
            // 1) grab the QR bitmap
            val context = LocalContext.current
            val qrBitmap = generateQrCode(uId)

            if(qrBitmap != null) {
                // 2) show the Qr image
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Your Business Card QR Code",
//                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 3) DOWNload button
                Button(onClick = {
                    saveImageToGallery(context, qrBitmap, "Business_Card")
                }) {
                    Text("Download QR Code")
                }
            } else{
                    Text("Failed to generate QR code")
                }
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
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Logout")
            }
        }
    }
}

// Helper function to save image to gallery
fun saveImageToGallery(context: Context, bitmap: Bitmap, filename: String) {
    val fos: OutputStream?
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore on Android 10+
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri: Uri? = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
            fos = uri?.let { context.contentResolver.openOutputStream(it) }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            uri?.let { context.contentResolver.update(it, values, null, null) }
        } else {
            // Legacy external storage for pre‑Q
            val imagesDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .apply { mkdirs() }
            val imageFile = File(imagesDir, "$filename.png")
            fos = FileOutputStream(imageFile)
            // refresh gallery
            context.sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
            )
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "Saved QR code to gallery", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save QR code: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("CardOwnerScreen", "saveImageToGallery error", e)
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
