package com.tim.listing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.tim.listing.app.ui.compose.ScooterDetailsScreenComposable
import com.tim.listing.app.ui.compose.ScooterListScreenComposable
import com.tim.listing.app.ui.navigation.Argument
import com.tim.listing.app.ui.navigation.Route
import com.tim.listing.app.ui.navigation.asRouteDefinition
import com.tim.listing.app.ui.navigation.resolved
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.vm.ListingDetailsViewModel
import com.tim.listing.app.ui.vm.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

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
    val currentRoute by navController.currentBackStackEntryFlow.map {
        it.destination.route
    }.collectAsState(Route.ScooterList.value)

    /**
     * The checkbox for availability can be done several ways, in a more complex app I would argue that
     * the ViewModel is a good place to keep state like this, which would then push out a new list of filtered state
     * based on whether the checkbox is checked or not.
     * I decided however to keep this in the compose space by using a LocalComposition and letting the
     * checkbox in the top bar change this mutable state, so that the List can filter based on it.
     *
     * Now that the List Composables have been moved out of this file however, the use of the LocalComposition
     * becomes a little less intuitive, as ScooterListScreen Compasables will need to know that this local composition
     * is available and use it to check whether to filter on AvailabilityState.
     *
     * If I were to refactor this around a bit I would move this into the viewModel for the ListScreen.
     */
    val showUnavailable = remember { mutableStateOf(true) }


    CompositionLocalProvider(LocalShowUnavailable provides ShowUnavailable(showUnavailable.value)) {
        AppTheme {
            Scaffold(
                topBar = {
                    // Use different Top Bars based on route.
                    when (currentRoute) {
                        Route.ScooterList.asRouteDefinition() -> ListTopBar(navController, onCloseApp) { showUnavailable.value = it }
                        Route.ScooterDetails.asRouteDefinition() -> DetailsTopBar(navController)
                        else -> ListTopBar(navController, onCloseApp) { showUnavailable.value = it }
                    }
                }
            ) { padding ->
                ScooterNavHost(padding, navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopBar(navController: NavHostController, onCloseApp: () -> Unit, onShowUnavailableToggle: (Boolean) -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(R.string.top_bar_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
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
        actions = {
            ScooterListTopBarActions(onShowUnavailableToggle)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(R.string.top_bar_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
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
                        navController.navigateUp()
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
fun ScooterListTopBarActions(onCheckedChange: ((Boolean) -> Unit)) {
    Text(
        stringResource(R.string.show_unavailable),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(56.dp)
    )
    Checkbox(checked = LocalShowUnavailable.current.show, onCheckedChange = {
        onCheckedChange(it)
    })
}

@Composable
fun ScooterNavHost(padding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.ScooterList.asRouteDefinition(),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(padding)
    ) {
        composable(Route.ScooterList.value) {
            ScooterListScreen(onShowDetails = { id ->
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
fun ScooterListScreen(onShowDetails: (Long) -> Unit) {
    val vm = hiltViewModel<ListingViewModel>()
    val uiState by vm.uiState().collectAsState(ListingViewModel.UiListState.Loading)
    ScooterListScreenComposable(uiState, onShowDetails = onShowDetails)
}

@Preview
@Composable
fun MainActivityPreview() {
    MainActivityScreen {}
}

val LocalShowUnavailable = compositionLocalOf { ShowUnavailable(true) }

data class ShowUnavailable(val show: Boolean)