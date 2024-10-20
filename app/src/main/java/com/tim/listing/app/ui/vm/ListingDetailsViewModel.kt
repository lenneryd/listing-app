package com.tim.listing.app.ui.vm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tim.listing.app.domain.usecase.ScooterUseCase
import com.tim.listing.app.ui.navigation.Argument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailsViewModel @Inject constructor(
    private val useCase: ScooterUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private val TAG = ListingViewModel::class.java.simpleName
    }

    private val uiState: MutableStateFlow<UiDetailsState> = MutableStateFlow(UiDetailsState.Loading)
    fun uiState(): Flow<UiDetailsState> = uiState


    val id = savedStateHandle.get<Long>(Argument.Id.value)
        ?: throw IllegalArgumentException("$TAG requires non-null ${Argument.Id.value} parameter.")

    init {
        viewModelScope.launch {
            val result = useCase.getScooter(id).first()
            result.fold(
                onSuccess = { model ->
                    uiState.value = UiDetailsState.ScooterDetails(
                        model.name,
                        model.id,
                        model.totalRides,
                        availabilityState = model.toAvailabilityState(),
                        battery = model.toBatteryState()
                    )
                },
                onFailure = { e ->
                    uiState.value = UiDetailsState.Error
                    Log.w(TAG, "Error loading scooters: ${e.message}")

                    e.printStackTrace()
                }
            )
        }
    }

    sealed class UiDetailsState {
        data object Loading : UiDetailsState()
        data object Error : UiDetailsState()

        data class ScooterDetails(
            val name: String,
            val id: Long,
            val rides: Long?,
            val availabilityState: AvailabilityState,
            val battery: BatteryState
        ) : UiDetailsState()
    }
}