package com.paywith.offersdemo.data.network

import com.paywith.offersdemo.data.model.CustomerDto
import com.paywith.offersdemo.data.model.CustomerWrapper
import com.paywith.offersdemo.data.model.OfferDto
import com.paywith.offersdemo.domain.model.SearchQuery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("persistence_layer/search")
    suspend fun getOffersByQuery(@Body searchQuery: SearchQuery): Response<List<OfferDto>>

    @POST("customers/sign_in")
    suspend fun userLogin(@Body customerWrapper: CustomerWrapper): Response<CustomerDto>
}