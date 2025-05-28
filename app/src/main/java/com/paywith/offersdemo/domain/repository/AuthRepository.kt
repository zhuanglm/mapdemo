package com.paywith.offersdemo.domain.repository

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.CustomerSignUp

interface AuthRepository {
    suspend fun login(phone: String, password: String): ApiResponse<CustomerSignUp>
}