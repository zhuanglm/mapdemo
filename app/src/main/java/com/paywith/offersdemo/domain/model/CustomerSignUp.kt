package com.paywith.offersdemo.domain.model

import java.util.UUID

/**
 * Represents a customer's sign-up information.
 *
 * @property id The unique identifier for the customer.
 * @property firstName The customer's first name.
 * @property lastName The customer's last name.
 * @property email The customer's email address.
 * @property createdAt The timestamp when the customer was created.
 * @property updatedAt The timestamp when the customer was last updated.
 * @property mobileNumber The customer's mobile phone number.
 * @property verificationCode The code used for verifying the customer's account.
 * @property dateOfBirth The customer's date of birth.
 * @property password The customer's chosen password.
 * @property passwordConfirmation The confirmation of the customer's password.
 * @property zipCode The customer's zip code.
 */

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