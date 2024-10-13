package com.tim.listing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import com.tim.listing.app.ui.theme.batteryColor
import com.tim.listing.app.ui.vm.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {

                val vm = hiltViewModel<ListingViewModel>()
                val uiState by vm.uiState().collectAsState(ListingViewModel.UiState.Loading)
                ScooterScreen(uiState)
            }
        }
    }
}

@Composable
fun ScooterScreen(state: ListingViewModel.UiState) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (state) {
            ListingViewModel.UiState.Error -> Text("Failed to return scooters")
            ListingViewModel.UiState.Loading -> CircularProgressIndicator()
            is ListingViewModel.UiState.ScooterDetails -> TODO()
            is ListingViewModel.UiState.ScooterList -> ScooterList(
                state,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScooterList(state: ListingViewModel.UiState.ScooterList, modifier: Modifier) {
    Column {
        Text(state.name)
        LazyColumn(modifier) {
            items(items = state.scooters, key = { item -> item.id }) { item ->
                ScooterItem(item, modifier = Modifier.animateItemPlacement())
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ScooterItem(scooter: ListingViewModel.ListScooterUi, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(all = 8.dp)
    ) {
        Text(scooter.name, style = MaterialTheme.typography.titleMedium)
        scooter.rides?.let { rides ->
            Text("Rides: $rides", style = MaterialTheme.typography.titleSmall)
        }

        when (scooter.batteryState) {
            is ListingViewModel.BatteryState.HasBattery -> BatteryComposable(scooter.batteryState)
            is ListingViewModel.BatteryState.OutOfBattery -> NoBatteryComposable()
        }

    }
}

@Composable
fun BatteryComposable(batteryState: ListingViewModel.BatteryState.HasBattery) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .padding(all = 4.dp), onDraw = {
            drawArc(
                color = batteryColor(batteryState.battery),
                startAngle = 270f,
                sweepAngle = sweepAngle(batteryState.battery),
                useCenter = true,
                style = Fill
            )
        })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.battery),
                contentDescription = "Battery Image",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(batteryState.batteryText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun NoBatteryComposable() {
    Column(horizontalAlignment = Alignment.End) {
        Icon(painterResource(R.drawable.battery_off), contentDescription = "No Battery Icon", Modifier.padding(end = 6.dp).size(24.dp), tint = MaterialTheme.colorScheme.error)
        Text("No Battery", style = MaterialTheme.typography.labelSmall)
    }
}

fun sweepAngle(percent: Float) = 360.0f * percent


@Preview
@Composable
fun ScooterScreenPreview() {
    AppTheme {
        ScooterScreen(
            state = ListingViewModel.UiState.ScooterList(
                name = "Stockholm",
                scooters = listOf(
                    ListingViewModel.ListScooterUi(
                        4,
                        "Scooter B4",
                        ListingViewModel.BatteryState.HasBattery(0.4f, "40%"),
                        ListingViewModel.AvailabilityState.WorkingAvailable,
                        12
                    ),
                    ListingViewModel.ListScooterUi(
                        8,
                        "Scooter D8",
                        ListingViewModel.BatteryState.HasBattery(0.64f, "64%"),
                        ListingViewModel.AvailabilityState.WorkingAvailable,
                        35
                    ),
                    ListingViewModel.ListScooterUi(
                        7,
                        "Scooter D7",
                        ListingViewModel.BatteryState.HasBattery(0.9f, "90%"),
                        ListingViewModel.AvailabilityState.WorkingAvailable,
                        800
                    ),
                    ListingViewModel.ListScooterUi(
                        12,
                        "Scooter G7",
                        ListingViewModel.BatteryState.OutOfBattery,
                        ListingViewModel.AvailabilityState.WorkingAvailable,
                        800
                    )
                )
            )
        )
    }
}