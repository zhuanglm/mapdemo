package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.CustomerSignUp
import com.paywith.offersdemo.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Project: Offers Demo
 * File: LoginUseCase
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Use case for handling user login.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Invokes the login use case to authenticate a user.
     *
     * This function attempts to log in a user with the provided email and password
     * by calling the `login` method of the [AuthRepository].
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     * @return An [ApiResponse] containing either the [CustomerSignUp] details on successful login
     *         or an error if the login fails.
     */
    suspend operator fun invoke(email: String, password: String): ApiResponse<CustomerSignUp> {
        return repository.login(email, password)
    }
}
