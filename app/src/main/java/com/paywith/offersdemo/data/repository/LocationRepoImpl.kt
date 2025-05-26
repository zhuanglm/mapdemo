package com.paywith.offersdemo.data.repository

import android.location.Location
import com.paywith.offersdemo.data.location.LocationProvider

import com.paywith.offersdemo.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepoImpl @Inject constructor(
    private val locationProvider: LocationProvider
) : LocationRepository {
    override suspend fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }
}
