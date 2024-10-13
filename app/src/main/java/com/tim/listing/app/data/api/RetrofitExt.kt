package com.tim.listing.app.data.api

import retrofit2.Response

fun <T : Any> Response<T>.toResult(): Result<T> = this.toResult { it }

/**
 * Map the Retrofit response to a Result<>, returning a failure if the response does not appropriately map to the provided type.
 */
fun <T : Any, R : Any> Response<T>.toResult(mapping: (T) -> R): Result<R> {
    return if (this.isSuccessful) {
        val value = this.body()
        if (value != null) {
            try {
                Result.success(mapping(value))
            } catch (e: Exception) {
                Result.failure(FailedToMapResponse(e))
            }
        } else {
            Result.failure(NetworkException(this.errorBody()?.toString(), this.code()))
        }
    } else {
        Result.failure(NetworkException(this.errorBody()?.toString(), this.code()))
    }
}

class NetworkException(val errorBody: String?, val errorCode: Int) : Exception()
class NoResponseException() : Exception()
class FailedToMapResponse(source: Exception) : Exception(source)