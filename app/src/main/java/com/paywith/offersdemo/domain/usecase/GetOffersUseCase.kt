package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.repository.OffersRepository
import javax.inject.Inject

class GetOffersUseCase @Inject constructor(
    private val repository: OffersRepository
) {
    suspend operator fun invoke(query: SearchQuery): ApiResponse<List<Offer>> = repository.getOffersByQuery(query)
}