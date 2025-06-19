package com.paywith.offersdemo.data.repository

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.paywith.offersdemo.data.location.LocationProvider
import com.paywith.offersdemo.data.model.SearchRegion
import com.paywith.offersdemo.domain.repository.LocationRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepoImpl @Inject constructor(
    private val locationProvider: LocationProvider,
    private val placesClient: PlacesClient
) : LocationRepository {
    override suspend fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }

    //**********google Place API implementation
    override suspend fun searchRegions(query: String): List<SearchRegion> =
        suspendCoroutine { continuation ->
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val results = response.autocompletePredictions.map {
                        SearchRegion(
                            description = it.getFullText(null).toString(),
                            placeId = it.placeId
                        )
                    }
                    continuation.resume(results)
                }
                .addOnFailureListener {
                    continuation.resume(emptyList())
                }
        }

    override suspend fun fetchLatLngFromPlaceId(placeId: String): LatLng? = suspendCoroutine { continuation ->
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                continuation.resume(response.place.latLng)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

}
