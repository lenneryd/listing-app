package com.tim.listing.app.data.repo

import com.tim.listing.app.data.api.ScooterApi
import com.tim.listing.app.data.api.toResult
import com.tim.listing.app.domain.model.ScooterResponseModel
import com.tim.listing.app.domain.model.toScooterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScooterRepository @Inject constructor(private val api: ScooterApi) {

    fun getScooters(): Flow<Result<ScooterResponseModel>> = flow {
        /**
         * Here we have the option of adding another layer, with a LocalStorage type solution of some format.
         * It allows us to cache and reuse responses, which can make sense as a way to ease the network load
         * when for example using [com.tim.listing.app.domain.usecase.ScooterUseCase.getScooter].
         * With only the single endpoint to work with we can cache the list response and use the cached response
         * to return any individual scooter might get requested by the above usecase method.
         * The requirement would be a way to clear the cache (such as for example time or a cache-busting parameter),
         * and a coroutine/flow-based api by the LocalStorage implementation.
         *
         * I have left implementation of the LocalStorage out for this exercise but will happily talk about it in a technical interview.
         **/

        emit(api.getScooters().toResult { entity ->
            entity.toScooterResponse()
        })
    }

}