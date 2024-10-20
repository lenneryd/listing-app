package com.tim.listing.app.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tim.listing.app.LocalShowUnavailable
import com.tim.listing.app.R
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.theme.batteryHigh
import com.tim.listing.app.ui.vm.AvailabilityState
import com.tim.listing.app.ui.vm.BatteryState
import com.tim.listing.app.ui.vm.ListingViewModel
import com.tim.listing.app.ui.vm.toBatteryText

@Composable
fun ScooterListScreenComposable(state: ListingViewModel.UiListState, onShowDetails: (Long) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        when (state) {
            ListingViewModel.UiListState.Error -> ErrorScreen(stringResource(R.string.list_screen_error), paddingValues)
            ListingViewModel.UiListState.Loading -> LoadingScreen(paddingValues)
            is ListingViewModel.UiListState.ScooterList -> ScooterList(
                state,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues),
                onShowDetails
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScooterList(state: ListingViewModel.UiListState.ScooterList, modifier: Modifier, onClicked: (Long) -> Unit) {
    Column(modifier) {
        LocalShowUnavailable.current.show.let { showUnavailable ->
            LazyColumn {
                items(
                    items = state.scooters.filter {
                        showUnavailable || it.availability is AvailabilityState.WorkingAvailable
                    },
                    key = { item -> item.id }) { item ->
                    ScooterItem(item, modifier = Modifier.animateItemPlacement(), onClicked = onClicked)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ScooterItem(scooter: ListingViewModel.ListScooterUi, modifier: Modifier, onClicked: (Long) -> Unit) {
    val available = scooter.availability is AvailabilityState.WorkingAvailable
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(
                if (available) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceContainerLow,
            )
            .padding(all = 8.dp)
            .clickable { onClicked(scooter.id) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.width(130.dp)) {
            Text(scooter.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(end = 4.dp))
            if (available) {
                Icon(painterResource(R.drawable.check), contentDescription = "Checkmark", tint = batteryHigh, modifier = Modifier.size(24.dp))
            }
        }
        if (scooter.rides != null) {
            Column(verticalArrangement = Arrangement.Center) {
                Row {
                    Icon(painterResource(R.drawable.bike), contentDescription = "Rides Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        "${scooter.rides} ${stringResource(R.string.list_screen_item_rides_postfix)}", style = MaterialTheme.typography.titleSmall, modifier = Modifier
                            .width(80.dp)
                            .padding(horizontal = 6.dp)
                    )
                }
            }
        }


        // Right edge icons
        when (scooter.availability) {
            is AvailabilityState.OutOfBattery -> NoBatteryComposable()
            is AvailabilityState.BrokenUnavailable -> BrokenComposable()
            else -> {
                when (scooter.batteryState) {
                    is BatteryState.HasBattery -> ListItemBatteryDisplay(scooter.batteryState)
                    is BatteryState.OutOfBattery -> NoBatteryComposable()
                }
            }
        }
    }
}

@Composable
fun ListItemBatteryDisplay(state: BatteryState.HasBattery) {
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxHeight()
            .width(84.dp)
    ) {
        BatteryArcComposable(state.battery)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.battery),
                contentDescription = "Battery Image",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text("${state.toBatteryText()}%", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun NoBatteryComposable() {
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxHeight()
            .width(84.dp)
    ) {
        Icon(
            painterResource(R.drawable.battery_off),
            contentDescription = "No Battery Icon",
            Modifier
                .padding(end = 6.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(stringResource(R.string.list_screen_item_no_battery), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun BrokenComposable() {
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxHeight()
            .width(84.dp)
    ) {
        Icon(painterResource(R.drawable.tools), contentDescription = "Broken icon", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp))
        Text(stringResource(R.string.list_screen_item_unavailable), style = MaterialTheme.typography.labelSmall)
    }
}

fun sweepAngle(percent: Float) = 360.0f * percent


@Preview
@Composable
fun ScooterScreenPreview() {
    AppTheme {
        ScooterListScreenComposable(
            state = ListingViewModel.UiListState.ScooterList(
                name = "Stockholm",
                scooters = listOf(
                    ListingViewModel.ListScooterUi(
                        4,
                        "Scooter B4",
                        BatteryState.HasBattery(0.4f),
                        AvailabilityState.WorkingAvailable,
                        12
                    ),
                    ListingViewModel.ListScooterUi(
                        8,
                        "Scooter D8",
                        BatteryState.HasBattery(0.64f),
                        AvailabilityState.WorkingAvailable,
                        35
                    ),
                    ListingViewModel.ListScooterUi(
                        7,
                        "Scooter D7",
                        BatteryState.HasBattery(0.9f),
                        AvailabilityState.WorkingAvailable,
                        800
                    ),
                    ListingViewModel.ListScooterUi(
                        12,
                        "Scooter G7",
                        BatteryState.OutOfBattery,
                        AvailabilityState.WorkingAvailable,
                        800
                    )
                )
            ),
            onShowDetails = { }
        )
    }
}

@Preview
@Composable
fun ScooterScreenLoadingPreview() {
    AppTheme {
        ScooterListScreenComposable(
            state = ListingViewModel.UiListState.Loading
        ) {}
    }
}

@Preview
@Composable
fun ScooterScreenErrorPreview() {
    AppTheme {
        ScooterListScreenComposable(
            state = ListingViewModel.UiListState.Error
        ) {}
    }
}