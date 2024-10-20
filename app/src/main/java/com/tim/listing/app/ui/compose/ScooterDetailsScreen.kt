package com.tim.listing.app.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tim.listing.app.R
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.theme.batteryColor
import com.tim.listing.app.ui.theme.batteryHigh
import com.tim.listing.app.ui.vm.AvailabilityState
import com.tim.listing.app.ui.vm.BatteryState
import com.tim.listing.app.ui.vm.ListingDetailsViewModel
import com.tim.listing.app.ui.vm.toBatteryText

@Composable
fun ScooterDetailsScreenComposable(state: ListingDetailsViewModel.UiDetailsState) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (state) {
            ListingDetailsViewModel.UiDetailsState.Error -> ErrorScreen(stringResource(R.string.details_screen_error), innerPadding)
            ListingDetailsViewModel.UiDetailsState.Loading -> LoadingScreen(innerPadding)
            is ListingDetailsViewModel.UiDetailsState.ScooterDetails -> ScooterDetails(
                state,
                innerPadding
            )
        }
    }
}

@Composable
fun ScooterDetails(state: ListingDetailsViewModel.UiDetailsState.ScooterDetails, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            // Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(top = 48.dp)
            ) {
                Text(state.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.requiredWidth(IntrinsicSize.Max))
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 36.dp))
            }

            // Scooter Image
            Image(
                painterResource(R.drawable.ic_launcher_round),
                contentDescription = "Scooter image",
                modifier = Modifier
                    .size(120.dp)
                    .shadow(5.dp, shape = CircleShape)
            )
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(top = 48.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))

            ScooterStatus(state)

            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
            when (state.availabilityState) {
                is AvailabilityState.BrokenUnavailable ->
                    WarningMessage(stringResource(R.string.details_screen_warning_mechanic))

                is AvailabilityState.OutOfBattery ->
                    WarningMessage(stringResource(R.string.details_screen_warning_battery))

                is AvailabilityState.WorkingUnavailable ->
                    WarningMessage(stringResource(R.string.details_screen_warning_in_use))

                else -> {}
            }

            ScooterActions(state.availabilityState is AvailabilityState.WorkingAvailable)
        }
    }
}

@Composable
fun ScooterStatus(state: ListingDetailsViewModel.UiDetailsState.ScooterDetails) {
    Row(
        modifier = Modifier
            .height(180.dp)
            .padding(all = 16.dp)
    ) {
        Column(modifier = Modifier.weight(0.5f)) {
            Row {
                Text("${stringResource(R.string.details_battery_label)} ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                when (state.battery) {
                    is BatteryState.HasBattery -> Text(state.battery.toBatteryText(), style = MaterialTheme.typography.labelLarge, color = batteryColor(state.battery.battery))
                    is BatteryState.OutOfBattery -> Text(
                        stringResource(R.string.details_battery_insufficient),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(Modifier.padding(top = 16.dp)) {
                Text(
                    "${stringResource(R.string.details_rides_label)} ${state.rides ?: stringResource(R.string.details_rides_not_available)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        VerticalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        Column(modifier = Modifier.weight(0.5f)) {
            Row {
                Text(
                    stringResource(R.string.details_available_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 4.dp)
                )
                if (state.availabilityState is AvailabilityState.WorkingAvailable) {
                    Icon(painterResource(R.drawable.check), contentDescription = "Available checkmark", tint = batteryHigh, modifier = Modifier.size(22.dp))
                } else {
                    Icon(painterResource(R.drawable.close), contentDescription = "Unavailable icon", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun ScooterActions(enabled: Boolean) {
    Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.padding(top = 16.dp, bottom = 48.dp)) {
        ElevatedButton(onClick = {}, enabled = enabled, colors = ButtonDefaults.elevatedButtonColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Text(stringResource(R.string.details_rent_button), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Preview
@Composable
fun ScooterDetailsWithBatteryPreview() {
    AppTheme {
        ScooterDetailsScreenComposable(
            ListingDetailsViewModel.UiDetailsState.ScooterDetails(
                "Scooter B4",
                4,
                null,
                AvailabilityState.WorkingAvailable,
                battery = BatteryState.HasBattery(0.35f)
            )
        )
    }
}

@Preview
@Composable
fun ScooterDetailsScreenPreview() {
    AppTheme {
        ScooterDetailsScreenComposable(
            ListingDetailsViewModel.UiDetailsState.ScooterDetails(
                "Scooter B6",
                6,
                12,
                AvailabilityState.OutOfBattery,
                battery = BatteryState.OutOfBattery
            )
        )
    }
}

@Preview
@Composable
fun ScooterDetailsScreenBrokenPreview() {
    AppTheme {
        ScooterDetailsScreenComposable(
            ListingDetailsViewModel.UiDetailsState.ScooterDetails(
                "Scooter B9",
                9,
                null,
                AvailabilityState.BrokenUnavailable,
                battery = BatteryState.OutOfBattery
            )
        )
    }
}
