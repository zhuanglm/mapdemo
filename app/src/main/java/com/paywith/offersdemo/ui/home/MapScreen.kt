package com.paywith.offersdemo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.AppStandardScreen
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.offers.LocationPermissionHandler
import com.paywith.offersdemo.ui.offers.OfferItem
import com.paywith.offersdemo.ui.offers.OffersScreenContent
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
    val showSortDialog = remember { mutableStateOf(false) }
    val showFilterDialog = remember { mutableStateOf(false) }
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

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 128.dp,
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                ) {
                    OffersBottomSheet(offers, onItemClick = onItemClick)
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


                // 排序 / 筛选弹窗
                if (showSortDialog.value) {
                    SortDialog(onDismiss = { showSortDialog.value = false })
                }

                if (showFilterDialog.value) {
                    FilterDialog(onDismiss = { showFilterDialog.value = false })
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

@Composable
fun ButtonsRow(
    modifier: Modifier = Modifier,
    leftButtonText: String,
    rightButtonText: String,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    onLeftClick: () -> Unit = { },
    onRightClick: () -> Unit = { },
    roundedCorner: Dp = 5.dp
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val buttonHeight = 40.dp
        ElevatedButton(
            onClick = onLeftClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
            shape = RoundedCornerShape(roundedCorner),
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leftIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = leftButtonText,
                    maxLines = 1
                )
            }
        }

        ElevatedButton(
            onClick = onRightClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
            shape = RoundedCornerShape(roundedCorner),
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                rightIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = rightButtonText,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchButtonsRowPreview() {
    ButtonsRow(
        leftButtonText = stringResource(R.string.search_merchants),
        rightButtonText = stringResource(R.string.where_to),
    )
}

@Preview(showBackground = true)
@Composable
fun FielterButtonsRowPreview() {
    ButtonsRow(
        leftIcon = ImageVector.vectorResource(R.drawable.ic_sort),
        rightIcon = ImageVector.vectorResource(R.drawable.ic_filter_eat),
        leftButtonText = stringResource(R.string.closest),
        rightButtonText = stringResource(R.string.all),
        roundedCorner = 30.dp
    )
}

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


@Composable
fun OffersBottomSheet(
    offers: List<OfferUiModel>,
    onSortClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ButtonsRow(
            leftIcon = ImageVector.vectorResource(R.drawable.ic_sort),
            rightIcon = ImageVector.vectorResource(R.drawable.ic_filter_eat),
            leftButtonText = stringResource(R.string.closest),
            rightButtonText = stringResource(R.string.all),
            roundedCorner = 30.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OffersScreenContent(
            offers = offers,
            modifier = Modifier.weight(1f),
            onOfferClick = { offer ->
                onItemClick(offer.offerId)
            }
        )
    }
}


@Composable
fun MerchantMapBox(
    modifier: Modifier = Modifier,
    offer: OfferUiModel,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 0.dp, bottom = 16.dp, start = 32.dp, end = 32.dp )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    ) {
        OfferItem(
            obs = offer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            onClick = { onClick(offer.offerId) }
        )
    }
}

@Composable
fun SortDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort Options") },
        text = { Text("Radio button list will go here") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}

@Composable
fun FilterDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Options") },
        text = { Text("Radio button list will go here") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
