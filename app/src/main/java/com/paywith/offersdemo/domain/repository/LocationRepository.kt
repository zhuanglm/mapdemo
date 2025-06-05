package com.paywith.offersdemo.domain.repository

import android.location.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?

    //Google Places SDK does not provide suspend API for autocomplete search
    suspend fun searchRegions(query: String): List<String>
}
