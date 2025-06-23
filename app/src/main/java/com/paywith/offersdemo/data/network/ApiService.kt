package com.paywith.offersdemo.data.network

import com.paywith.offersdemo.data.model.CustomerDto
import com.paywith.offersdemo.data.model.CustomerWrapper
import com.paywith.offersdemo.data.model.OfferDto
import com.paywith.offersdemo.data.model.OfferTags
import com.paywith.offersdemo.domain.model.SearchQuery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Project: Offers Demo
 * File: ApiService
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Interface defining API endpoints for the Offers Demo application.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

interface ApiService {
    @POST("persistence_layer/search")
    suspend fun getOffersByQuery(@Body searchQuery: SearchQuery): Response<List<OfferDto>>

    @POST("customers/sign_in")
    suspend fun userLogin(@Body customerWrapper: CustomerWrapper): Response<CustomerDto>

    /**
     * load tag types (offer filters)
     */
    @GET("tag_types")
    suspend fun getOfferTags(): Response<OfferTags>
}