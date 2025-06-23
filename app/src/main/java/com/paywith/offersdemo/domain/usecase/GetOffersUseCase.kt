package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.repository.OffersRepository
import javax.inject.Inject
/**
 * Project: Offers Demo
 * File: GetOffersUseCase
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Use case for retrieving offers based on a search query from the repository.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
class GetOffersUseCase @Inject constructor(
    private val repository: OffersRepository
) {
    suspend operator fun invoke(query: SearchQuery): ApiResponse<List<Offer>> = repository.getOffersByQuery(query)
}