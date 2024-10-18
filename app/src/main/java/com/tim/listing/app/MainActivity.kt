package com.tim.listing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.navigation.Argument
import com.tim.listing.app.ui.navigation.Route
import com.tim.listing.app.ui.navigation.asRouteDefinition
import com.tim.listing.app.ui.navigation.resolved
import com.tim.listing.app.ui.theme.batteryColor
import com.tim.listing.app.ui.vm.ListingDetailsViewModel
import com.tim.listing.app.ui.vm.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainActivityScreen { finish() }
        }
    }
}

@Composable
fun MainActivityScreen(onCloseApp: () -> Unit) {
    val navController = rememberNavController()

    AppTheme {
        Scaffold(
            topBar = { NavigationTopBar(navController, onCloseApp) }
        ) { padding ->
            ScooterNavHost(padding, navController)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(navController: NavHostController, onCloseApp: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Scooter Listing",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Arrow",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false)
                    ) {
                        if (!navController.navigateUp()) {
                            onCloseApp()
                        }
                    }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
fun ScooterNavHost(padding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.ScooterList.asRouteDefinition(),
        modifier = Modifier.background(MaterialTheme.colorScheme.primary).padding(padding)
    ) {
        composable(Route.ScooterList.value) {
            ScooterScreen(onShowDetails = { id ->
                navController.navigate(Route.ScooterDetails.resolved(id = id))
            })
        }

        composable(Route.ScooterDetails.asRouteDefinition(),
            arguments = listOf(
                navArgument(Argument.Id.value) {
                    type = NavType.LongType
                }
            )
        ) {
            // Argument.Id provided to SavedStateHandle.
            ScooterDetailsScreen()
        }
    }
}

@Composable
fun ScooterDetailsScreen() {
    val vm = hiltViewModel<ListingDetailsViewModel>()
    val uiState by vm.uiState().collectAsState(ListingDetailsViewModel.UiDetailsState.Loading)
    ScooterDetailsScreenComposable(uiState)
}

@Composable
fun ScooterDetailsScreenComposable(state: ListingDetailsViewModel.UiDetailsState) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (state) {
            ListingDetailsViewModel.UiDetailsState.Error -> Text("Failed to show specific scooter.", modifier = Modifier.padding(innerPadding))
            ListingDetailsViewModel.UiDetailsState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
            is ListingDetailsViewModel.UiDetailsState.ScooterDetails -> ScooterDetails(
                state,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ScooterDetails(state: ListingDetailsViewModel.UiDetailsState.ScooterDetails, modifier: Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Text("Details page for scooter: ${state.name}")
    }
}

@Composable
fun ScooterScreen(onShowDetails: (Long) -> Unit) {
    val vm = hiltViewModel<ListingViewModel>()
    val uiState by vm.uiState().collectAsState(ListingViewModel.UiListState.Loading)
    ScooterScreenComposable(uiState, onShowDetails = onShowDetails)
}

@Composable
fun ScooterScreenComposable(state: ListingViewModel.UiListState, onShowDetails: (Long) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (state) {
            ListingViewModel.UiListState.Error -> Text("Failed to return scooters")
            ListingViewModel.UiListState.Loading -> CircularProgressIndicator()
            is ListingViewModel.UiListState.ScooterList -> ScooterList(
                state,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                onShowDetails
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScooterList(state: ListingViewModel.UiListState.ScooterList, modifier: Modifier, onClicked: (Long) -> Unit) {
    Column(modifier) {
        LazyColumn {
            items(items = state.scooters, key = { item -> item.id }) { item ->
                ScooterItem(item, modifier = Modifier.animateItemPlacement(), onClicked = onClicked)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ScooterItem(scooter: ListingViewModel.ListScooterUi, modifier: Modifier, onClicked: (Long) -> Unit) {
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
            .clickable { onClicked(scooter.id) }
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
        Icon(
            painterResource(R.drawable.battery_off),
            contentDescription = "No Battery Icon",
            Modifier
                .padding(end = 6.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text("No Battery", style = MaterialTheme.typography.labelSmall)
    }
}

fun sweepAngle(percent: Float) = 360.0f * percent


@Preview
@Composable
fun ScooterScreenPreview() {
    AppTheme {
        ScooterScreenComposable(
            state = ListingViewModel.UiListState.ScooterList(
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
            ),
            onShowDetails = { }
        )
    }
}

@Preview
@Composable
fun ScooterDetailsScreenPreview() {
    AppTheme {
        ScooterDetailsScreenComposable(
            ListingDetailsViewModel.UiDetailsState.ScooterDetails(
                "Scooter B4",
                4,
                12
            )
        )
    }
}