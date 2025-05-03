package com.example.dummy_database.ui.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import com.example.dummy_database.R

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How to Use",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 22.sp
        )

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
                    .aspectRatio(1f)
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(16.dp)
        )

        // ✅ Add the Back Button inside the Column
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
