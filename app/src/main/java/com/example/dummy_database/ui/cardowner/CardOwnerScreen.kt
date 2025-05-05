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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dummy_database.R
import com.example.dummy_database.ui.network.ConnectivityStatus
import com.example.dummy_database.ui.network.rememberConnectivityState
import com.example.dummy_database.utils.generateQrCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CardOwnerScreen(
    onBackClick: () -> Unit
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val connectivityStatus = rememberConnectivityState()
    val context = LocalContext.current

    // form fields
    var linkedInUrl by remember { mutableStateOf("") }
    var introduction by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }

    // errors
    var linkedInError by remember { mutableStateOf<String?>(null) }
    var linkedInFetchError by remember { mutableStateOf<String?>(null) }
    var githubError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var introductionError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // avatar & voice
    var selectedAvatar by remember { mutableStateOf<String?>(null) }
    val avatarOptions = listOf("avatar_man", "avatar_woman")
    var voicePreference by remember { mutableStateOf<String?>(null) }
    val avatarVoices = listOf("MALE", "FEMALE")

    // card design
    var selectedDesign by remember { mutableStateOf<Int?>(null) }
    val designOptions = listOf(
        R.drawable.card_option1,
        R.drawable.card_option2,
        R.drawable.card_option3
    )
    val CARD_RATIO = 1.6f

    val uid = auth.currentUser?.uid.orEmpty()
    var uId by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    var qrVisible by remember { mutableStateOf(false) }

    BackHandler {
        showLogoutConfirm = true
    }

    fun isValidHost(rawUrl: String, requiredHost: String): Boolean {
        return try {
            val normalized = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
            val host = Uri.parse(normalized).host?.lowercase() ?: return false
            host == requiredHost || host.endsWith(".$requiredHost")
        } catch (_: Exception) { false }
    }

    fun generateCardBitmap(
        context: Context,
        designResId: Int,
        uId: String,
        targetWidth: Int = 800  // or whatever max size fits your screen reasonably
    ): Bitmap? {
        // 1. Load full-resolution background
        val originalBg = BitmapFactory.decodeResource(context.resources, designResId) ?: return null

        // 2. Calculate scaled height
        val aspectRatio = originalBg.height.toFloat() / originalBg.width
        val targetHeight = (targetWidth * aspectRatio).toInt()

        // 3. Resize background
        val bg = Bitmap.createScaledBitmap(originalBg, targetWidth, targetHeight, true)

        // 4. Generate QR code
        val qr = generateQrCode(uId) ?: return null
        val qrPx = (bg.width / 4f).toInt()
        val scaledQr = Bitmap.createScaledBitmap(qr, qrPx, qrPx, true)

        // 5. Compose final bitmap
        val result = Bitmap.createBitmap(bg.width, bg.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(bg, 0f, 0f, paint)

        // 6. Center QR
        val left = (bg.width - qrPx) / 2f
        val top = (bg.height - qrPx) / 2f
        canvas.drawBitmap(scaledQr, left, top, paint)

        return result
    }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            try {
                val doc = db.collection("cardholders").document(uid).get().await()
                if (doc.exists()) {
                    qrVisible = true
                    linkedInUrl = doc.getString("linkedInUrl").orEmpty()
                    introduction = doc.getString("introduction").orEmpty()
                    education = doc.getString("education").orEmpty()
                    experience = doc.getString("experience").orEmpty()
                    hobbies = doc.getString("hobbies").orEmpty()
                    githubUrl = doc.getString("githubUrl").orEmpty()
                    gmail = doc.getString("gmail").orEmpty()
                    voicePreference = doc.getString("voicePreference")
                    selectedAvatar = doc.getString("avatarId")
                    selectedDesign = doc.getString("designOption")?.toIntOrNull()
                    uId = uid
                }
            } catch (e: Exception) {
                Log.w("CardOwnerScreen", "Error fetching existing doc: ", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7D8A5)) // Set the background colour here
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dialogs
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Oops!") },
                    text = { Text(alertMessage) },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) { Text("OK") }
                    }
                )
            }
            if (showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    title = { Text("Confirm logout") },
                    text = { Text("Have you saved your changes?") },
                    confirmButton = {
                        Button(onClick = {
                            auth.signOut()
                            onBackClick()
                        }) { Text("Yes, Logout") }
                    },
                    dismissButton = {
                        Button(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
                    }
                )
            }
            if (showSaveSuccess) {
                AlertDialog(
                    onDismissRequest = { showSaveSuccess = false },
                    title = { Text("Saved!") },
                    text = { Text("Your changes have been saved.") },
                    confirmButton = {
                        Button(onClick = { showSaveSuccess = false }) { Text("OK") }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Edit Your Business Card!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D4C41)
                )
            }


            // LinkedIn URL + Fetch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = linkedInUrl,
                        onValueChange = {
                            linkedInUrl = it
                            linkedInError = null
                        },
                        label = { Text("LinkedIn URL") },
                        isError = linkedInError != null,
                        supportingText = {
                            Column {
                                Text("Enter your LinkedIn URL, formatted as linkedin.com/in/username")
                                linkedInError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
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
                            else if (!isValidHost(linkedInUrl, "linkedin.com/in/")) {
                                linkedInError = "Must be a valid linkedin.com URL"
                            }
                            else {
                                // 2) pretend‐fetch → fill the dummies
                                introduction = "Hi, I’m John, a business strategist helping companies scale and thrive. Let’s discuss how I can drive growth for your organization. Let’s connect!"
                                education     = "I completed my BSc in Business Administration from University of Toronto."
                                experience    = "I worked as a business strategist for 2 years at IBM."
                                hobbies       = "I love playing football and learning new things."
                                // 3) then show the API‐not‐available message
                                linkedInFetchError = "LinkedIn API isn’t available. Below fields are filled with placeholder data. Please, update the placeholder data as required."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A4D4D)), // brown
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Fetch from LinkedIn", color = Color.White, fontSize = 18.sp)
                    }
                    linkedInFetchError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            // Introduction + other fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = introduction,
                        onValueChange = {
                            introduction = it; introductionError = null; errorMessage = ""
                        },
                        label = { Text("Introduction") },
                        isError = introductionError != null,
                        supportingText = {
                            Column {
                                Text(
                                    "This is the first thing your avatar will say when your card is scanned. Please introduce yourself and write in first person as if speaking about yourself.",
                                    color = Color(0xFF5D4037)
                                )
                                introductionError?.let {
                                    Text(
                                        it,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = education,
                        onValueChange = { education = it },
                        label = { Text("Education") },
                        modifier = Modifier.padding(top = 16.dp),
                        supportingText = {
                            Column {
                                Text(
                                    "Please write in first person as if speaking about yourself.",
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Experience") },
                        modifier = Modifier.padding(top = 16.dp),
                        supportingText = {
                            Column {
                                Text(
                                    "Please write in first person as if speaking about yourself.",
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = hobbies,
                        onValueChange = { hobbies = it },
                        label = { Text("Hobbies") },
                        modifier = Modifier.padding(top = 16.dp),
                        supportingText = {
                            Column {
                                Text(
                                    "Please write in first person as if speaking about yourself.",
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = githubUrl,
                        onValueChange = {
                            githubUrl = it
                            githubError = null
                        },
                        label = { Text("GitHub URL") },
                        isError = githubError != null,
                        supportingText = {
                            Column {
                                Text("Enter your GitHub URL, formatted as github.com/username")
                                githubError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = gmail,
                        onValueChange = { gmail = it; emailError = null },
                        label = { Text("Email") },
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let {
                                Text(
                                    it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Voice Preference
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Voice Preference:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        color = Color(0xFF5D4037)
                    )

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avatarVoices.forEach { voice ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                RadioButton(
                                    selected = voicePreference == voice,
                                    onClick = { voicePreference = voice })
                                Text(if (voice == "MALE") "Male" else "Female")
                            }
                        }
                    }

                    // Avatar
                    Text(
                        "Select Avatar:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        color = Color(0xFF5D4037)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avatarOptions.forEach { avatar ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RadioButton(
                                    selected = selectedAvatar == avatar,
                                    onClick = { selectedAvatar = avatar }
                                )
                                Image(
                                    painter = painterResource(
                                        id = if (avatar == "avatar_man") R.drawable.male_image else R.drawable.female_image
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .aspectRatio(1f) // Force square
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Design
                    Text(
                        "Select Card Design:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        color = Color(0xFF5D4037)
                    )

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        designOptions.forEach { resId ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                RadioButton(
                                    selected = selectedDesign == resId,
                                    onClick = { selectedDesign = resId })
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(85.dp)
                                        .aspectRatio(CARD_RATIO),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    // Save
                    Button(
                        onClick = {
                            introductionError = null; linkedInError = null; githubError =
                            null; emailError = null; errorMessage = ""
                            if (connectivityStatus == ConnectivityStatus.Unavailable) {
                                errorMessage = "Saving failed! Please go online and try again."
                                return@Button
                            }
                            if (introduction.isBlank()) {
                                introductionError =
                                    "Please complete your introduction for your AR business card."
                                errorMessage = "Saving failed! Introduction must not be empty."
                                return@Button
                            }
                            if (selectedDesign == null) {
                                alertMessage = "Please select a card design before saving."
                                showDialog = true
                                return@Button
                            }
                            if (selectedAvatar == null) {
                                alertMessage = "Please select an avatar before saving."
                                showDialog = true
                                return@Button
                            }
                            if (voicePreference == null) {
                                alertMessage = "Please select a voice before saving."
                                showDialog = true
                                return@Button
                            }
                            var linkOk = true
                            if (linkedInUrl.isNotBlank() && !isValidHost(
                                    linkedInUrl,
                                    "linkedin.com/in/"
                                )
                            ) {
                                linkedInError = "Must be a valid linkedin.com URL"; linkOk =
                                    false
                            }
                            if (githubUrl.isNotBlank() && !isValidHost(
                                    githubUrl,
                                    "github.com/"
                                )
                            ) {
                                githubError = "Must be a valid github.com URL"; linkOk = false
                            }
                            if (gmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                                    gmail
                                )
                                    .matches()
                            ) {
                                emailError = "Must be a valid email address"; linkOk = false
                            }
                            if (!linkOk) {
                                errorMessage =
                                    "Saving failed! Please fix the highlighted links."
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
                                "avatarId" to selectedAvatar,
                                "designOption" to selectedDesign?.toString()
                            )
                            db.collection("cardholders").document(uid).set(data)
                                .addOnSuccessListener {
                                    uId = uid
                                    showSaveSuccess = true
                                    qrVisible = true
                                }
                                .addOnFailureListener { e ->
                                    errorMessage =
                                        if (e is com.google.firebase.FirebaseNetworkException)
                                            "Saving failed! Please go online and try again."
                                        else
                                            "Saving failed: ${e.localizedMessage ?: "Unknown error"}"
                                }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A4D4D)), // brown
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Account Details", color = Color.White, fontSize = 18.sp)
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Display designed card with QR
                    if (qrVisible && uId.isNotEmpty() && selectedDesign != null) {
                        // 2) Remember the generated bitmap, using context, uId & design as keys
                        val cardBitmap: Bitmap? = remember(context, uId, selectedDesign) {
                            generateCardBitmap(context, selectedDesign!!, uId, targetWidth = 800)
                        }

                        // 3) Display it
                        cardBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Generated Business Card",
                                modifier = Modifier
                                    .width(250.dp) // Match your design option width
                                    .aspectRatio(bmp.width / bmp.height.toFloat()),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    saveImageToGallery(
                                        context,
                                        bmp,
                                        "My_Business_Card"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF7A4D4D
                                    )
                                )
                            ) {
                                Text("Download Business Card", fontSize = 18.sp)
                            }
                        } ?: Text("Failed to generate card preview")
                    }
                }
            }

            // Logout
            if (auth.currentUser != null) {
                Button(
                    onClick = { showLogoutConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A4D4D)), // brown
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Logout", fontSize = 18.sp)
                }
            }
        }
    }
}

fun saveImageToGallery(context: Context, cardBitmap: Bitmap, filename: String) {
    var fos: OutputStream? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KnowMeBetter")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            fos = uri?.let { context.contentResolver.openOutputStream(it) }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            uri?.let { context.contentResolver.update(it, values, null, null) }
        } else {
            val imagesDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .apply { mkdirs() }
            val imageFile = File(imagesDir, "$filename.png")
            fos = FileOutputStream(imageFile)
            context.sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
            )
        }
        fos?.use { out ->
            cardBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            Toast.makeText(context, "Saved card to gallery", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save card: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("CardOwnerScreen", "saveBitmap error", e)
    }
}

/**
 * Builds a single Bitmap by drawing the design background
 * and then overlaying the user’s QR code centered on top.
 *
 * @param context    used to load the design drawable
 * @param designResId  R.drawable.card_optionX
 * @param uId        the string you pass into generateQrCode()
 * @param qrSizeDp   size of the QR code in dp (default 150)
 * @return           combined card Bitmap or null on failure
 */
fun generateCardBitmap(
    context: Context,
    designResId: Int,
    uId: String
): Bitmap? {
    // 1) load background
    val bg = BitmapFactory.decodeResource(context.resources, designResId) ?: return null

    // 2) generate QR bitmap
    val qr = generateQrCode(uId) ?: return null

    // 3) scale QR to occupy ~1/4 of card width
    val qrPx = (bg.width / 4f).toInt()
    val scaledQr = Bitmap.createScaledBitmap(qr, qrPx, qrPx, true)

    // 4) compose final bitmap
    val result = Bitmap.createBitmap(bg.width, bg.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    canvas.drawBitmap(bg, 0f, 0f, paint)

    // 5) center QR
    val left = (bg.width  - qrPx) / 2f
    val top  = (bg.height - qrPx) / 2f
    canvas.drawBitmap(scaledQr, left, top, paint)

    return result
}



@Composable
fun QrCodeImage(uId: String) {
    val bitmap = generateQrCode(uId)
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
