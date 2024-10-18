package com.tim.listing.app.domain.usecase

import com.tim.listing.app.data.repo.ScooterRepository
import com.tim.listing.app.domain.model.ScooterModel
import com.tim.listing.app.domain.model.ScooterResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScooterUseCase @Inject constructor(private val repo: ScooterRepository) {
    /**
     * Returns a [ScooterResponseModel] in a [Result.success] or api request error in a [Result.failure].
     */
    fun getScooters(): Flow<Result<ScooterResponseModel>> = repo.getScooters()

    /**
     * Returns an individual scooter in a [Result.success] object, or on failure will
     * return an [Result.failure] with either the api request error or an [ElementNotFoundException] if
     * request was successful but the id does not exist in the response.
     */
    fun getScooter(id: Long): Flow<Result<ScooterModel>> = getScooters().map { result ->
        result.fold(
            onSuccess = { response ->
                response.scooters.firstOrNull { scooter -> scooter.id == id }
                    ?.let { Result.success(it) } ?: Result.failure(
                    ElementNotFoundException("Element with id: $id cannot be found in the returned response")
                )
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}

class ElementNotFoundException(message: String) : Exception(message)