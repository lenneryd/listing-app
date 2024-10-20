package com.tim.listing.app.ui.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tim.listing.app.R

@Composable
fun LoadingScreen(paddingValues: PaddingValues = PaddingValues()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(text: String, paddingValues: PaddingValues = PaddingValues()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        WarningMessage(text)
    }
}

@Composable
fun WarningMessage(text: String) {
    Row(
        modifier = Modifier
            .padding(all = 16.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(8.dp))
            .padding(all = 16.dp)

    ) {
        Icon(
            painterResource(R.drawable.alert_circle_outline),
            contentDescription = "Warning icon",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(26.dp)
                .padding()
        )
        Text(
            text,
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}