package com.tim.listing.app.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tim.listing.app.domain.usecase.ScooterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ListingViewModel @Inject constructor(private val useCase: ScooterUseCase) : ViewModel() {
    companion object {
        private val thresholdUnavailableBattery = 0.1
    }

    private val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    fun uiState(): Flow<UiState> = uiState

    init {
        viewModelScope.launch {
            val result = useCase.getScooters().first()

            when {
                result.isSuccess -> {
                    uiState.value = UiState.ScooterList(
                        name = result.getOrThrow().name,
                        scooters = result.getOrThrow().scooters.map { scooter ->
                            val batteryState = if (scooter.battery < thresholdUnavailableBattery) {
                                BatteryState.OutOfBattery
                            } else {
                                BatteryState.HasBattery(
                                    scooter.battery,
                                    "${(scooter.battery * 100).roundToInt()}%"
                                )
                            }
                            ListScooterUi(
                                id = scooter.id,
                                name = scooter.name,
                                batteryState = batteryState,
                                availability = scooter.let {
                                    when {
                                        scooter.needFix -> AvailabilityState.BrokenUnavailable
                                        scooter.inUse -> AvailabilityState.WorkingUnavailable
                                        batteryState is BatteryState.OutOfBattery -> AvailabilityState.OutOfBattery
                                        else -> AvailabilityState.WorkingAvailable
                                    }
                                },
                                scooter.totalRides
                            )
                        }
                    )
                }

                result.isFailure -> {
                    uiState.value = UiState.Error
                }
            }
        }
    }


    sealed class UiState {
        data object Loading : UiState()
        data object Error : UiState()

        data class ScooterList(
            val name: String,
            val scooters: List<ListScooterUi>
        ) : UiState()

        data class ScooterDetails(
            val name: String,
            val id: Long,
            val rides: Long?
        ) : UiState()
    }

    sealed class BatteryState {
        data class HasBattery(val battery: Float, val batteryText: String) : BatteryState()
        data object OutOfBattery : BatteryState()
    }

    sealed class AvailabilityState {
        data object WorkingAvailable : AvailabilityState()
        data object WorkingUnavailable : AvailabilityState()
        data object BrokenUnavailable : AvailabilityState()
        data object OutOfBattery : AvailabilityState()
    }

    data class ListScooterUi(
        val id: Long,
        val name: String,
        val batteryState: BatteryState,
        val availability: AvailabilityState,
        val rides: Long?
    )
}
