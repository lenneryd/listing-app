package com.tim.listing.app.ui.vm

import com.tim.listing.app.domain.model.ScooterModel
import kotlin.math.roundToInt

const val thresholdUnavailableBattery = 0.1

sealed class BatteryState {
    data class HasBattery(val battery: Float) : BatteryState()
    data object OutOfBattery : BatteryState()
}

sealed class AvailabilityState {
    data object WorkingAvailable : AvailabilityState()
    data object WorkingUnavailable : AvailabilityState()
    data object BrokenUnavailable : AvailabilityState()
    data object OutOfBattery : AvailabilityState()
}

fun ScooterModel.hasBattery(): Boolean = battery > thresholdUnavailableBattery

fun ScooterModel.toBatteryState() = if (!hasBattery()) {
    BatteryState.OutOfBattery
} else {
    BatteryState.HasBattery(
        battery
    )
}
fun BatteryState.HasBattery.toBatteryText() = "${(battery * 100).roundToInt()}%"

fun ScooterModel.toAvailabilityState() = this.let {
    when {
        needFix -> AvailabilityState.BrokenUnavailable
        inUse -> AvailabilityState.WorkingUnavailable
        hasBattery() -> AvailabilityState.WorkingAvailable
        else -> AvailabilityState.OutOfBattery
    }
}