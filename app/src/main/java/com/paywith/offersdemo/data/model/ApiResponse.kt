package com.paywith.offersdemo.data.model

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Failure(val message: String?, val exception: Throwable? = null) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
}
