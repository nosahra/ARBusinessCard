package com.example.dummy_database.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }  // Toggle between login/register

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = if (isLoginMode) "Login" else "Register")

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 characters)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Check that fields are not empty
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

//                    // Check that password is at least 6 characters
//                    if (password.length < 6) {
//                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
//                        return@Button
//                    }


                    if (isLoginMode) {
                        // --- LOGIN FLOW ---
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && user.isEmailVerified) {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please verify your email first. Check your inbox for the link.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        auth.signOut()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Login failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        // --- REGISTER FLOW ---
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verifyTask ->
                                            if (verifyTask.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Verification email sent to ${user.email}. Please check your inbox.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                // switch to login so they can sign in once they've verified:
                                                isLoginMode = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to send verification email: ${verifyTask.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Registration failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = if (isLoginMode) "Login" else "Register")
            }

            Spacer(Modifier.height(8.dp))

            // Button to toggle mode
            Button(
                onClick = { isLoginMode = !isLoginMode },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = if (isLoginMode) "Switch to Register" else "Switch to Login")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Back")
            }
        }
    }
}

fun onBackClick() {
    TODO("Not yet implemented")
}
