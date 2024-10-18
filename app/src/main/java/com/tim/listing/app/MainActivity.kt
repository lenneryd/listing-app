package com.tim.listing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.tim.listing.app.ui.theme.AppTheme
import com.tim.listing.app.ui.navigation.Argument
import com.tim.listing.app.ui.navigation.Route
import com.tim.listing.app.ui.navigation.asRouteDefinition
import com.tim.listing.app.ui.navigation.resolved
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
    val showUnavailable = remember { mutableStateOf(true) }
    CompositionLocalProvider(LocalShowUnavailable provides ShowUnavailable(showUnavailable.value)) {
        AppTheme {
            Scaffold(
                topBar = { NavigationTopBar(navController, onCloseApp, { showUnavailable.value = it }) }
            ) { padding ->
                ScooterNavHost(padding, navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(navController: NavHostController, onCloseApp: () -> Unit, onShowUnavailableToggle: (Boolean) -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Scooters",
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

@Composable
fun ScooterListTopBarActions(onCheckedChange: ((Boolean) -> Unit)) {
    val checked = remember { mutableStateOf(true) }
    Text(
        "Show unavail.",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(56.dp)
    )
    Checkbox(checked = checked.value, onCheckedChange = {
        checked.value = it
        onCheckedChange(it)
    }, modifier = Modifier)
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

val LocalShowUnavailable = compositionLocalOf { ShowUnavailable(true) }
data class ShowUnavailable(val show: Boolean)