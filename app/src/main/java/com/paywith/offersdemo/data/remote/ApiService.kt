package com.paywith.offersdemo.data.remote

import com.paywith.offersdemo.data.model.OfferDto
import com.paywith.offersdemo.domain.model.SearchQuery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("persistence_layer/search")
    suspend fun getOffersByQuery(@Body searchQuery: SearchQuery): Response<List<OfferDto>>
}