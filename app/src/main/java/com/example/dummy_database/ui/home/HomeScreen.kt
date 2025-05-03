
package com.example.dummy_database.ui.home


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
import com.google.firebase.auth.FirebaseAuth


@Composable
fun HomeScreen(
    onCardOwnerScreenClick: () -> Unit,
    onScannerScreenClick: () -> Unit,
    onNeedAuth: () -> Unit,
    onHelp: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Title section
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Know Me Better",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E4E4E),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your Business Card, Reimagined in AR",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF9575CD),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Buttons section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Button(
                onClick = onNeedAuth,
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                Text("Register/Login", color = Color.White, fontSize = 18.sp)
            }

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

            Button(
                onClick = onScannerScreenClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5AB))
            ) {
                Text("Scan a QR Code", color = Color(0xFF6E4E4E), fontSize = 18.sp)
            }

            Button(
                onClick = onHelp,
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB39DDB))
            ) {
                Text("Help", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
