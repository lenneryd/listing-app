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
        emit(api.getScooters().toResult { entity ->
            entity.toScooterResponse()
        })
    }

}