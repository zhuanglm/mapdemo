package com.paywith.offersdemo.domain.repository

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.CustomerSignUp
/**
 * Project: Offers Demo
 * File: AuthRepository
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Interface defining authentication-related operations.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
interface AuthRepository {
    suspend fun login(phone: String, password: String): ApiResponse<CustomerSignUp>
}