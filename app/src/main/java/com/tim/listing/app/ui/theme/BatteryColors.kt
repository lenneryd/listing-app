package com.tim.listing.app.ui.theme

import androidx.compose.ui.graphics.Color

val Green300 = Color(0xFF81C784)
val Orange200 = Color(0xFFFFCC80)
val DeepOrange400 = Color(0xFFFF7043)

val batteryHigh = Green300
val batteryMedium = Orange200
val batteryLow = DeepOrange400

fun batteryColor(fraction: Float) = when {
    fraction > 0.8 -> batteryHigh
    fraction > 0.5 -> batteryMedium
    else -> batteryLow
}