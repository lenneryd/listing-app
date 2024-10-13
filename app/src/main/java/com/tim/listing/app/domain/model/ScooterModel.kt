package com.tim.listing.app.domain.model

import com.tim.listing.app.data.model.ScooterResponseEntity

data class ScooterResponseModel(
    val name: String,
    val scooters: List<ScooterModel>
)

data class ScooterModel(
    val id: Long,
    val name: String,
    val battery: Float,
    val inUse: Boolean,
    val needFix: Boolean,
    val totalRides: Long?
)

fun ScooterResponseEntity.toScooterResponse(): ScooterResponseModel = ScooterResponseModel(
    name = this.name,
    scooters = this.scooters.map {
        ScooterModel(
            id = it.id,
            name = it.name,
            battery = it.battery,
            inUse = it.inUse,
            needFix = it.needFix ?: false,
            totalRides = it.totalRides
        )
    }
)
