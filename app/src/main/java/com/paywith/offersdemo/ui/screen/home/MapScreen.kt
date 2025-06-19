package com.paywith.offersdemo.ui.screen.home

/**
 * Project: Offers Demo
 * Company: paywith.com
 * File: MapScreen.kt
 * Created: 2025-06-06
 * Developer: Ray Z
 *
 * Description:
 * This file defines the `MapScreen` composable, which serves as the main home screen of the app.
 * It integrates a Google Map view with markers representing merchant offers, a BottomSheet
 * showing offer lists, and top-level actions for search and location targeting.
 *
 * Key features include:
 * - `BottomSheetScaffold` to show expandable offer listings.
 * - `GoogleMapView` with interactive markers and camera state management.
 * - `MerchantMapBox` to preview the currently selected offer.
 * - Lifecycle-aware location fetching via `AppViewModel`.
 * - ButtonsRow for search and location actions.
 *
 * This screen is the central UI for users to explore location-based offers.
 *
 * All rights reserved © paywith.com.
 */

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.LocationPermissionHandler
import com.paywith.offersdemo.ui.component.AppStandardScreen
import com.paywith.offersdemo.ui.component.ButtonsRow
import com.paywith.offersdemo.ui.component.MerchantMapBox
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A flag indicating whether the current camera movement is triggered programmatically.
 * isProgrammaticAnimationInProgress
 * This is used to distinguish between user-initiated map gestures (e.g. panning, zooming)
 * and automatic camera animations (e.g. focusing on a marker or initial map centering).
 *
 * When true, UI logic such as deselecting offers or loading nearby data should be skipped,
 * since the map is moving due to internal logic rather than user interaction.
 *
 * This helps prevent unintended side effects during animations and ensures smooth UX.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    appViewModel: AppViewModel,
    onItemClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onWhereToClick: () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val selectedOffer = remember { mutableStateOf<OfferUiModel?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isProgrammaticAnimationInProgress = remember { mutableStateOf(false) }
    val hasRequestedLocation = rememberSaveable { mutableStateOf(false) }

    LocationPermissionHandler(
        onPermissionGranted = {
            if (!hasRequestedLocation.value) {
                hasRequestedLocation.value = true
                appViewModel.fetchLocation()
            }
        }
    )

    val location by appViewModel.locationFlow.collectAsState()
    val offersResponse by appViewModel.offers.collectAsState()
    val offers = (offersResponse as? ApiResponse.Success)?.data ?: emptyList()
    val filterOptions = appViewModel.getFilterOptions()
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()
    val targetLocation by appViewModel.targetLocation

    // 🔵 get current location
    LaunchedEffect(location) {
        location?.let { loc ->
            snackbarHostState.showSnackbar("Location: ${loc.latitude}, ${loc.longitude}")
        }
    }

    // 🔵 monitoring camera movement and reset selected offer
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collectLatest { isMoving ->
                if (isMoving && !isProgrammaticAnimationInProgress.value) {
                    selectedOffer.value = null
                }
            }
    }

    //move camera to target location
    LaunchedEffect(targetLocation) {
            targetLocation?.let {
            isProgrammaticAnimationInProgress.value = true
            try {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it.latLng, it.zoom))
            } finally {
                isProgrammaticAnimationInProgress.value = false
            }
        }
    }

    AppStandardScreen(
        title = stringResource(R.string.locations),
        snackbarHostState = snackbarHostState,
    ) { padding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 128.dp,
            sheetContent = {
                Box(Modifier.fillMaxWidth().heightIn(max = 450.dp)) {
                    BottomSheet(
                        scaffoldState = scaffoldState,
                        offers = offers,
                        filterOptions = filterOptions,
                        onItemClick = onItemClick,
                        onSortClick = { appViewModel.updateSort(it) },
                        onFilterClick = { appViewModel.updateFilter(it) }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMapView(
                    offers = offers,
                    cameraPositionState = cameraPositionState,
                    selectedOffer = selectedOffer.value,
                    onMarkerClick = { offer ->
                        selectedOffer.value = offer
                        offer.merchantLocation?.let {
                            appViewModel.setTargetLocation(LatLng(it.latitude, it.longitude), zoom = 18f)
                        }
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                    }
                )

                selectedOffer.value?.let {
                    MerchantMapBox(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 100.dp),
                        offer = it,
                        onClick = onItemClick
                    )
                }

                ButtonsRow(
                    modifier = Modifier.padding(top = 10.dp),
                    leftButtonText = stringResource(R.string.search_merchants),
                    onLeftClick = { onSearchClick() },
                    rightButtonText = stringResource(R.string.where_to),
                    onRightClick = { onWhereToClick() }
                )
            }
        }
    }
}
