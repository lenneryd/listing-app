package com.tim.listing.app.domain.usecase

import com.tim.listing.app.data.repo.ScooterRepository
import com.tim.listing.app.domain.model.ScooterResponseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScooterUseCase @Inject constructor(private val repo: ScooterRepository) {
    fun getScooters(): Flow<Result<ScooterResponseModel>> = repo.getScooters()
}