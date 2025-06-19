package com.paywith.offersdemo.domain.repository

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.paywith.offersdemo.data.model.SearchRegion

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?

    //Google Places SDK does not provide suspend API for autocomplete search
    suspend fun searchRegions(query: String): List<SearchRegion>

    suspend fun fetchLatLngFromPlaceId(placeId: String): LatLng?
}
