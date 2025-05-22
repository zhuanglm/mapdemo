package com.paywith.buoylocaldemo.data.remote

import com.paywith.buoylocaldemo.data.model.OfferDto
import com.paywith.buoylocaldemo.domain.model.SearchQuery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("persistence_layer/search")
    fun getOffersByQuery(@Body searchQuery: SearchQuery): Response<List<OfferDto>>
}