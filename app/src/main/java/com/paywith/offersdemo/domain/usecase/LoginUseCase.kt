package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.CustomerSignUp
import com.paywith.offersdemo.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): ApiResponse<CustomerSignUp> {
        return repository.login(email, password)
    }
}
