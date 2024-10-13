package com.tim.listing.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScooterResponseEntity(
    val name: String,
    val scooters: List<ScooterEntity>
)

@Serializable
data class ScooterEntity(
    val id: Long,
    val name: String,
    val battery: Float,
    @SerialName("in_use")
    val inUse: Boolean,
    @SerialName("need_fix")
    val needFix: Boolean?,
    @SerialName("total_rides")
    val totalRides: Long?
)

