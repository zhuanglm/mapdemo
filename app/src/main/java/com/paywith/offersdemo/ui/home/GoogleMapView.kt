package com.paywith.offersdemo.ui.home

/**
 * Project: Offers Demo
 * Company: paywith.com
 * File: GoogleMapView.kt
 * Created: 2025-06-05
 * Developer: Ray Z
 *
 * Description:
 * This file defines the composable wrapper for displaying a Google Map view inside a Jetpack Compose layout.
 * It handles lifecycle-aware binding between the MapView and the Android lifecycle, and supports basic map interactions
 * such as camera positioning and location display.
 *
 * All rights reserved © paywith.com.
 */

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.paywith.offersdemo.ui.model.OfferUiModel
import kotlinx.coroutines.launch

@Composable
fun GoogleMapView(
    offers: List<OfferUiModel>,
    cameraPositionState: CameraPositionState,
    isProgrammaticAnimationInProgress: MutableState<Boolean>,
    onMarkerClick: (OfferUiModel) -> Unit
) {
    // move to first offer
    LaunchedEffect(offers) {
        isProgrammaticAnimationInProgress.value = true
        if (offers.isNotEmpty()) {
            val firstLatLng = offers[0].merchantLocation?.let {
                LatLng(it.latitude, it.longitude)
            }
            try {
                firstLatLng?.let { CameraUpdateFactory.newLatLngZoom(it, 12f) }
                    ?.let { cameraPositionState.animate(it) }
            } finally {
                isProgrammaticAnimationInProgress.value = false
            }

        }
    }

    val coroutineScope = rememberCoroutineScope()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState

    ) {
        offers.forEach { offer ->

            val loc = offer.merchantLocation
            if (loc != null) {
                val currentLatLng = LatLng(loc.latitude, loc.longitude)

                Marker(
                    state = MarkerState(
                        position = currentLatLng
                    ),
                    title = offer.merchantName,
                    snippet = offer.merchantAddress,
                    onClick = {
                        onMarkerClick(offer)

                        coroutineScope.launch {
                            isProgrammaticAnimationInProgress.value = true
                            try {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f),
                                    durationMs = 600
                                )
                            } finally {
                                isProgrammaticAnimationInProgress.value = false
                            }
                        }
                        true
                    }
                )
            }
        }

    }
}
