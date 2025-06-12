package com.paywith.offersdemo.domain.usecase

import com.paywith.offersdemo.domain.model.Offer
import javax.inject.Inject

/**
 * Project: Offers Demo
 * File: FilterOffersUseCase
 * Created: 2025-06-12
 * Developer: Ray Z
 * Description: [filter tagType Offers]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

class FilterOffersUseCase @Inject constructor() {

    operator fun invoke(offers: List<Offer>, tagType: String): List<Offer> {
        if (offers.isEmpty() || tagType.isBlank()) return offers

        val filtered = offers.filter { it.tagType == tagType }

        return filtered.ifEmpty { offers }
    }
}