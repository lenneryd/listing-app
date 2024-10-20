package com.tim.listing.app.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.theme.batteryColor
import com.tim.listing.app.ui.theme.primaryContainerLight

@Composable
fun BatteryArcComposable(batteryFraction: Float, size: Dp = 28.dp) {
    Canvas(modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .padding(all = 4.dp), onDraw = {

        drawCircle(color = primaryContainerLight)
        drawArc(
            color = batteryColor(batteryFraction),
            startAngle = 270f,
            sweepAngle = sweepAngle(batteryFraction),
            useCenter = true,
            style = Fill
        )
    })
}

@Preview(showBackground = true)
@Composable
fun BatteryComposablePreview() {
    AppTheme {
        Row {
            BatteryArcComposable(0.8f)
            BatteryArcComposable(0.1f)
            BatteryArcComposable(0.5f)
            BatteryArcComposable(0.2f)
            BatteryArcComposable(0.9f)
            BatteryArcComposable(0.7f)
        }
    }
}