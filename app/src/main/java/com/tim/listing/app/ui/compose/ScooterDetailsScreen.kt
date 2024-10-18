package com.tim.listing.app.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tim.listing.app.ui.vm.ListingDetailsViewModel

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
    Column(modifier = modifier.fillMaxSize()) {
        Text("Details page for scooter: ${state.name}")
    }
}
