package com.example.dummy_database.ui.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dummy_database.R
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HelpScreen(onBackClick: () -> Unit) {
    val imageList = listOf(
        R.drawable.help_slide_ex,
        R.drawable.help_slide_ex,
        R.drawable.help_slide_ex,
        // Add more as needed
    )

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 🔙 Back icon at top-left
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color(0xFF6E4E4E)  // Brown arrow
                )
            }
        }

        // 📌 Title styled like home screen
        Text(
            text = "How to Use",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037), // Brown
            modifier = Modifier
                .padding(top = 7.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 📷 Slideshow
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Help slide ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
            )
        }

        // ⚪ Indicator
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}
