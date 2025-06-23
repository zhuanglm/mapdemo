package com.paywith.offersdemo.domain.repository

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.paywith.offersdemo.data.model.SearchRegion
/**
 * Project: Offers Demo
 * File: LocationRepository
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Interface for accessing location-related data from various sources.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Location?

    //Google Places SDK does not provide suspend API for autocomplete search
    suspend fun searchRegions(query: String): List<SearchRegion>

    suspend fun fetchLatLngFromPlaceId(placeId: String): LatLng?
}
