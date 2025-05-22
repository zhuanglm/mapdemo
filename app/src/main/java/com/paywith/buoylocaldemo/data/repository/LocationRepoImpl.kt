package com.paywith.buoylocaldemo.data.repository

import android.location.Location
import com.paywith.buoylocaldemo.data.location.LocationProvider

import com.paywith.buoylocaldemo.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepoImpl @Inject constructor(
    private val locationProvider: LocationProvider
) : LocationRepository {
    override suspend fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }
}
