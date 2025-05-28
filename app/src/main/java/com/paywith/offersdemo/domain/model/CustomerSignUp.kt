package com.paywith.offersdemo.domain.model

import java.util.UUID

data class CustomerSignUp(
    val id: UUID? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val mobileNumber: String? = null,
    val verificationCode: String? = null,
    val dateOfBirth: String? = null,
    val password: String? = null,
    val passwordConfirmation: String? = null,
    val zipCode: String? = null
)