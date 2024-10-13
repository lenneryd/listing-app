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
    private val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    fun uiState(): Flow<UiState> = uiState

    init {
        viewModelScope.launch {
            val result = useCase.getScooters().first()

            when {
                result.isSuccess -> {
                    uiState.value= UiState.ScooterList(
                        name = result.getOrThrow().name,
                        scooters = result.getOrThrow().scooters.map { ListScooterUi(it.id, it.name, it.battery, "${(it.battery * 100).roundToInt()}%",it.totalRides) }
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

    data class ListScooterUi(val id: Long, val name: String, val battery: Float, val batteryText: String, val rides: Long?)
}
