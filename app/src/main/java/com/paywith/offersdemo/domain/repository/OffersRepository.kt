package com.paywith.offersdemo.domain.repository

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.data.model.OfferTags
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery
/**
 * Project: Offers Demo
 * File: OffersRepository
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Interface defining the contract for accessing offer data.
 * It outlines the methods that need to be implemented by any concrete repository
 * responsible for fetching offers and offer tags.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
interface OffersRepository {
    suspend fun getOffersByQuery(query: SearchQuery): ApiResponse<List<Offer>>

    suspend fun getOfferTags(): ApiResponse<OfferTags>
}