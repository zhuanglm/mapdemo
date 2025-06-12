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

import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.getOfferMarkerIcon
import com.paywith.offersdemo.ui.model.OfferUiModel
import kotlinx.coroutines.launch


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun GoogleMapView(
    offers: List<OfferUiModel>,
    cameraPositionState: CameraPositionState,
    isProgrammaticAnimationInProgress: MutableState<Boolean>,
    selectedOffer: OfferUiModel?,
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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val nightModeFlags = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    val styleResId = when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> R.raw.mapstyle_night
        else -> R.raw.mapstyle_day
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        MapEffect { map ->
            val styleOptions = try {
                MapStyleOptions.loadRawResourceStyle(context, styleResId)
            } catch (e: Resources.NotFoundException) {
                Log.e("MapStyle", "Can't find style. Error: ", e)
                null
            }
            styleOptions?.let {
                val success = map.setMapStyle(it)
                if (!success) {
                    Log.e("MapStyle", "Style parsing failed.")
                }
            }
        }

        offers.forEach { offer ->

            val loc = offer.merchantLocation
            if (loc != null) {
                val currentLatLng = LatLng(loc.latitude, loc.longitude)

                val isSelected = offer.offerId == selectedOffer?.offerId

                val icon = getOfferMarkerIcon(offer, isSelected)

                Marker(
                    state = MarkerState(
                        position = currentLatLng
                    ),
                    title = offer.merchantName,
                    snippet = offer.merchantAddress,
                    icon = icon,
                    // lower Z-index to avoid overlapping with other markers
                    zIndex = if (isSelected) 1f else 0f,
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
