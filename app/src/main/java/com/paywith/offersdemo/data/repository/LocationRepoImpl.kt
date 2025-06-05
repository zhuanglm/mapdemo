package com.paywith.offersdemo.data.repository

import android.location.Location
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.paywith.offersdemo.data.location.LocationProvider

import com.paywith.offersdemo.domain.repository.LocationRepository
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine
import com.google.android.libraries.places.api.net.PlacesClient
import kotlin.coroutines.resume

class LocationRepoImpl @Inject constructor(
    private val locationProvider: LocationProvider,
    private val placesClient: PlacesClient
) : LocationRepository {
    override suspend fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }

    override suspend fun searchRegions(query: String): List<String> = suspendCoroutine { continuation ->
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val results = response.autocompletePredictions.map {
                    it.getFullText(null).toString()
                }
                continuation.resume(results)
            }
            .addOnFailureListener {
                continuation.resume(emptyList())
            }
    }
}
