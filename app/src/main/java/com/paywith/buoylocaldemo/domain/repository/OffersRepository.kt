package com.paywith.buoylocaldemo.domain.repository

import com.paywith.buoylocaldemo.domain.model.Offer
import com.paywith.buoylocaldemo.domain.model.SearchQuery

interface OffersRepository {
    suspend fun getOffersByQuery(query: SearchQuery): Result<List<Offer>>
}