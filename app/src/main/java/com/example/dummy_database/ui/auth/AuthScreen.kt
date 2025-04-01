package com.example.dummy_database.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = if (isLoginMode) "Login" else "Register")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 6 characters)") },
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = {
                // Check that fields are not empty
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (isLoginMode) {
                    // Log for debugging
                    Log.d("AuthScreen", "Attempting login with email: $email")
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                Log.d("AuthScreen", "Login successful")
                                onAuthSuccess()
                            } else {
                                val errMsg = task.exception?.message ?: "Unknown error"
                                Toast.makeText(context, "Login failed: $errMsg", Toast.LENGTH_LONG).show()
                                Log.w("AuthScreen", "Login failed", task.exception)
                            }
                        }
                } else {
                    // Log for debugging
                    Log.d("AuthScreen", "Attempting registration with email: $email")
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                Log.d("AuthScreen", "Registration successful")
                                onAuthSuccess()
                            } else {
                                val errMsg = task.exception?.message ?: "Unknown error"
                                Toast.makeText(context, "Registration failed: $errMsg", Toast.LENGTH_LONG).show()
                                Log.w("AuthScreen", "Registration failed", task.exception)
                            }
                        }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = if (isLoginMode) "Login" else "Register")
        }

        // Button to toggle mode
        Button(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = if (isLoginMode) "Switch to Register" else "Switch to Login")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Back")
        }
    }
}

fun onBackClick() {
    TODO("Not yet implemented")
}
