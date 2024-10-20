package com.tim.listing.app.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tim.listing.app.domain.usecase.ScooterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(private val useCase: ScooterUseCase) : ViewModel() {
    companion object {
        private val TAG = ListingViewModel::class.java.simpleName
    }

    private val uiState: MutableStateFlow<UiListState> = MutableStateFlow(UiListState.Loading)
    fun uiState(): Flow<UiListState> = uiState

    init {
        viewModelScope.launch {
            val result = useCase.getScooters().first()

            result.fold(
                onSuccess = { model ->
                    uiState.value = UiListState.ScooterList(
                        name = model.name,
                        scooters = model.scooters.map { scooter ->
                            ListScooterUi(
                                id = scooter.id,
                                name = scooter.name,
                                batteryState = scooter.toBatteryState(),
                                availability = scooter.toAvailabilityState(),
                                scooter.totalRides
                            )
                        }
                    )
                },
                onFailure = { e ->
                    uiState.value = UiListState.Error
                    Log.w(TAG, "Error loading scooters: ${e.message}")

                    e.printStackTrace()
                }

            )
        }
    }


    sealed class UiListState {
        data object Loading : UiListState()
        data object Error : UiListState()

        data class ScooterList(
            val name: String,
            val scooters: List<ListScooterUi>
        ) : UiListState()
    }

    data class ListScooterUi(
        val id: Long,
        val name: String,
        val batteryState: BatteryState,
        val availability: AvailabilityState,
        val rides: Long?
    )
}
