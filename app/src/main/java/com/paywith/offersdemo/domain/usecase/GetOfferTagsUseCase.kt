package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.data.model.OfferTags
import com.paywith.offersdemo.domain.repository.OffersRepository
import javax.inject.Inject

/**
 * Project: Offers Demo
 * File: GetOfferTagsUseCase
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

class GetOfferTagsUseCase @Inject constructor(
    private val repository: OffersRepository
) {
    suspend operator fun invoke(): ApiResponse<OfferTags> = repository.getOfferTags()
}
