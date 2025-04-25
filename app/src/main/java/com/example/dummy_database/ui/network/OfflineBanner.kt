package com.example.dummy_database.ui.network


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineBanner() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)

            .background(Color(0xFFB00020)), // a red tone
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "You are offline!",
            color = Color.White,
            fontSize = 14.sp
        )
    }
}