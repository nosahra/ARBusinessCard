package com.example.dummy_database.ui.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onCardOwnerScreenClick: () -> Unit,
    onScannerScreenClick: () -> Unit,
    onNeedAuth: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Business Card App")

        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    // Not logged in → go to Auth screen
                    onNeedAuth()
                } else {
                    // Already logged in → go straight to Cardholder
                    onCardOwnerScreenClick()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("CardOwner")
        }

        Button(
            onClick = onScannerScreenClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Scan My Card")
        }

//        if(FirebaseAuth.getInstance().currentUser != null){
//            // Logout Button (will only show, if user is logged in)
//            Button(onClick = {
//                FirebaseAuth.getInstance().signOut()
//                // Optionally navigate to AUTH_ROUTE
//            }) {
//                Text("Logout")
//            }
//        }
    }
}


