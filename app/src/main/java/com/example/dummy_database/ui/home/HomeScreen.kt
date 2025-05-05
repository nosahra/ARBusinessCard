/**
 * Defines the home screen UI for the AR business card application.
 * This screen provides navigation options for creating/editing a card,
 * scanning a card, and accessing help.
 *
 * Contribution:
 * Newton: Skeleton of the page
 * Sahra: Styling,layout and renaming labels. also added the help button
 */



package com.example.dummy_database.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontStyle
import com.example.dummy_database.R


/**
 * Composable function that renders the main home screen UI
 */
@Composable
fun HomeScreen(
    onCardOwnerScreenClick: () -> Unit,     //Callback when user wants to view or edit their own card
    onScannerScreenClick: () -> Unit,       //Callback to launch the QR-code scanner for viewing others' cards
    onNeedAuth: () -> Unit,                 //Callback when user needs to log in or register
    onHelp: () -> Unit,                     //Callback to show help overlay
) {
    // Full-screen container with white background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Top-left Help icon button (done by Sahra)
        androidx.compose.material3.IconButton(
            onClick = onHelp,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(20.dp)
        ) {
            androidx.compose.material3.Icon(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_info),
                contentDescription = "Help",
                tint = Color(0xFF6E4E4E)
            )
        }

        // 🔹 App Title
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 470.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Know Me Better",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E4E4E),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your Business Card, Reimagined in AR",
                fontSize = 15.5.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF9B7D7D),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 🔹 Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //label for registration/login
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Create") }
                    append(" or ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Edit") }
                    append(" your business card:")
                },
                fontSize = 14.sp,
                color = Color(0xFF9B7D7D),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Register/Login button
            Button(
                onClick = onNeedAuth,
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                Text("Register/Login", color = Color(0xFFF7D8A5), fontSize = 18.sp)
            }

            // label for QR code scanning
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("View") }
                    append(" a business card:")
                },
                fontSize = 14.sp,
                color = Color(0xFF9B7D7D),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // QR code scanning button
            Button(
                onClick = onScannerScreenClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7D8A5))
            ) {
                Text("Scan a QR Code", color = Color(0xFF6E4E4E), fontSize = 18.sp)
            }
        }
    }
}
