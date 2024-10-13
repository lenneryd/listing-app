package com.tim.listing.app.data.api

import com.tim.listing.app.data.model.ScooterResponseEntity
import retrofit2.Response
import retrofit2.http.GET

interface ScooterApi {

    // https://storage.googleapis.com/voi-android-technical-interview/scooters.json

    @GET("scooters.json")
    suspend fun getScooters(): Response<ScooterResponseEntity>

}