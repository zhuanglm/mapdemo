package com.paywith.offersdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.paywith.offersdemo.data.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * Project: Offers Demo
 * File: BaseViewModel
 * Created: 2025-06-11
 * Developer: Ray Z
 * Description:
 * Base ViewModel that provides common functionality:
 * - loading state
 * - error message handling
 * - API response handling (with and without data mapping)
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

open class BaseViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    protected fun showLoading() {
        _loading.value = true
    }

    protected fun hideLoading() {
        _loading.value = false
    }

    /**
     * Executes a suspend API call, maps the result to a new type [R],
     * and emits the final result to a [MutableStateFlow].
     *
     * @param flow The destination StateFlow to emit UI-friendly results into
     * @param sourceCall The original suspend API call returning ApiResponse<T>
     * @param mapper A suspend function to convert from T to R
     */
    protected suspend fun <T, R> emitMappedApiResponse(
        flow: MutableStateFlow<ApiResponse<R>>,
        sourceCall: suspend () -> ApiResponse<T>,
        mapper: suspend (T) -> R
    ) {
        showLoading()
        flow.value = ApiResponse.Loading
        try {
            when (val result = sourceCall()) {
                is ApiResponse.Success -> {
                    val mapped = withContext(Dispatchers.Default) { mapper(result.data) }
                    flow.value = ApiResponse.Success(mapped)
                }
                is ApiResponse.Failure -> {
                    flow.value = ApiResponse.Failure(result.message, result.exception)
                    showError(result.message)
                }
                is ApiResponse.Loading -> {
                    flow.value = ApiResponse.Loading
                }
            }
        } finally {
            hideLoading()
        }
    }

    /**
     * Executes a suspend API call and directly emits the ApiResponse<T>
     * into the given MutableStateFlow, with automatic error and loading handling.
     *
     * @param flow The destination StateFlow to emit into
     * @param sourceCall The suspend function that performs the API call
     */
    protected suspend fun <T> emitApiResponse(
        flow: MutableStateFlow<ApiResponse<T>>,
        sourceCall: suspend () -> ApiResponse<T>
    ) {
        showLoading()
        flow.value = ApiResponse.Loading
        try {
            when (val result = sourceCall()) {
                is ApiResponse.Success -> {
                    flow.value = result
                }
                is ApiResponse.Failure -> {
                    flow.value = result
                    showError(result.message)
                }
                is ApiResponse.Loading -> {
                    flow.value = ApiResponse.Loading
                }
            }
        } finally {
            hideLoading()
        }
    }

    private suspend fun showError(message: String?) {
        _errorMessage.emit(message ?: "Unknown error")
    }
}
