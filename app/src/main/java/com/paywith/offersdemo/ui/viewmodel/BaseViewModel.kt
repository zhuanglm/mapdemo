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
 * Description: [Add a brief description of the purpose of this file]
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
