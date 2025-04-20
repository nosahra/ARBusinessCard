
package com.example.dummy_database.ui.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onCardOwnerScreenClick: () -> Unit,
    onScannerScreenClick: () -> Unit,
    onNeedAuth: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Multi-line Text with Different Font Sizes
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.sp, // Larger font size for "KNOW ME BETTER"
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("KNOW ME BETTER\n") // First line
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 16.sp, // Smaller font size for the subtext
                    )
                ) {
                    append("An AR Business Card App") // Second line
                }
            },
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {

                onNeedAuth()

//                val user = FirebaseAuth.getInstance().currentUser
//                if (user == null) {
//                    // Not logged in → go to Auth screen
//                    onNeedAuth()
//                } else {
//                    // Already logged in → go straight to Cardholder
//                    onCardOwnerScreenClick()
//
//                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Create Your Card")
        }

        Button(
            onClick = onScannerScreenClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Scan A Card")
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
}


