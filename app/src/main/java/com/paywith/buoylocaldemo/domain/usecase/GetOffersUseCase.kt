package com.paywith.buoylocaldemo.domain.usecase

import com.paywith.buoylocaldemo.domain.model.Offer
import com.paywith.buoylocaldemo.domain.model.SearchQuery
import com.paywith.buoylocaldemo.domain.repository.OffersRepository
import javax.inject.Inject

class GetOffersUseCase @Inject constructor(
    private val repository: OffersRepository
) {
    suspend operator fun invoke(query: SearchQuery): List<Offer> = repository.getOffersByQuery(query)
}