package com.paywith.offersdemo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.CustomerSignUp
import com.paywith.offersdemo.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Project: Offers Demo
 * File: LoginViewModel
 * Created: 2025-06-11
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase
) : BaseViewModel() {
    private val _loginState = MutableStateFlow<ApiResponse<CustomerSignUp>>(ApiResponse.Loading)
    val loginState: StateFlow<ApiResponse<CustomerSignUp>> =_loginState

    fun userLogin(phone: String, password: String) {

        viewModelScope.launch {
            emitApiResponse(
                flow = _loginState,
                sourceCall = { login(phone, password) }
            )
        }
    }
}
