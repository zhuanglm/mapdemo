package com.paywith.offersdemo.ui.home

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.AppStandardScreen
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.offers.LocationPermissionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                appViewModel.onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LocationPermissionHandler(
        onPermissionGranted = {
            appViewModel.fetchLocation()
        }
    )

    val location by appViewModel.locationFlow.collectAsState()
    val offersResponse by appViewModel.offers.collectAsState()

    LaunchedEffect(location) {
        location?.let {
            snackbarHostState.showSnackbar("Location: ${it.latitude}, ${it.longitude}")
        }
    }

    AppStandardScreen(
        title = stringResource(R.string.locations),
        snackbarHostState = snackbarHostState,
    ) { padding ->
        val offers = (offersResponse as? ApiResponse.Success)?.data ?: emptyList()
        val filterOptions = appViewModel.getFilterOptions()

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 128.dp,
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                ) {
                    BottomSheet(
                        scaffoldState = scaffoldState,
                        offers,
                        filterOptions,
                        onItemClick = onItemClick,
                        onSortClick = {
                            appViewModel.updateSort(it)
                        },
                        onFilterClick = {
                            appViewModel.updateFilter(it)
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                location?.let { loc ->
                    val latLng = LatLng(loc.latitude, loc.longitude)

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(latLng, 12f)
                    }

                    // observing camera movement，clear selected Offer
                    LaunchedEffect(cameraPositionState) {
                        snapshotFlow { cameraPositionState.isMoving }
                            .collectLatest { isMoving ->
                                if (isMoving && !isProgrammaticAnimationInProgress.value) {
                                    selectedOffer.value = null
                                }
                            }
                    }

                    val coroutineScope = rememberCoroutineScope()

                    GoogleMapView(
                        offers = offers,
                        cameraPositionState = cameraPositionState,
                        isProgrammaticAnimationInProgress = isProgrammaticAnimationInProgress,
                        selectedOffer = selectedOffer.value,
                        onMarkerClick = { offer ->
                            selectedOffer.value = offer
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    )
                }

                selectedOffer.value?.let {
                    MerchantMapBox(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 100.dp), // Adjust based on BottomSheet height
                        offer = it,
                        onClick = onItemClick
                    )
                }

                // Top buttons over the map
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
