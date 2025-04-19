package com.example.dummy_database.ui.auth

import android.util.Log
import android.util.Patterns
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


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

    // error states
    var passwordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }


    fun isPasswordStrong(pw: String): Boolean {
        // at least 6 chars, at least one uppercase, at least one digit
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{6,}\$")
        return regex.matches(pw)
    }


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
            // TITLE
            Text(text = if (isLoginMode) "Login" else "Register")

            // General error (network failure, permission denied, etc)
            generalError?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                )
            }


            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Email") },
                isError = emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            emailError?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }


            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it
                                passwordError= null
                                },
                label = { Text("Password (min 6 characters)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                isError = passwordError != null
            )
            passwordError?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            // only show Confirm Password in Register mode
            if (!isLoginMode) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isError = confirmPasswordError != null
                )
                confirmPasswordError?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {

                    // clear old errors
                    emailError = null
                    passwordError = null
                    generalError = null

                    // Check that fields are not empty
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Email format check
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email format"
                        return@Button
                    }


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

                                        generalError =  "Invalid credentials. Please check email and password."
                                        Log.e("AuthScreen", "Login failed: ${task.exception?.message}")
                                }
                            }
                    } else {

                        // --- REGISTER FLOW ---

                        // Enforce strong password only on register
                        if (!isPasswordStrong(password)) {
                            passwordError = "Password must have at least 6 chars, an uppercase letter & a digit"
                            return@Button
                        }

                        // Confirm password
                        if (password != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            return@Button
                        }

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
                                        generalError = "Registration failed: ${task.exception?.localizedMessage}"
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
