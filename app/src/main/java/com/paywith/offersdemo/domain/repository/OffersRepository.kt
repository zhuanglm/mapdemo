package com.paywith.offersdemo.domain.repository

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery

interface OffersRepository {
    suspend fun getOffersByQuery(query: SearchQuery): ApiResponse<List<Offer>>
}