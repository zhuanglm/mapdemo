package com.paywith.offersdemo.ui.offers

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.AppStandardScreen
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.model.OfferUiModel


@Composable
fun OffersScreen(appViewModel: AppViewModel) {
    LocationPermissionHandler(
        onPermissionGranted = {
            appViewModel.fetchLocation()
        }
    )

    val location by appViewModel.locationFlow.collectAsState()
    val offersResponse by appViewModel.offers.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(location) {
        location?.let {
            snackbarHostState.showSnackbar("Location: ${it.latitude}, ${it.longitude}")
        }
    }

    AppStandardScreen(
        title = stringResource(R.string.offers),
        snackbarHostState = snackbarHostState,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        val offers = (offersResponse as? ApiResponse.Success)?.data ?: emptyList()
        OffersScreenContent(
            offers = offers,
            modifier = Modifier.padding(padding),
            onOfferClick = {}
        )
    }
}

@Composable
fun OffersScreenContent(
    modifier: Modifier = Modifier,
    offers: List<OfferUiModel>,
    onOfferClick: (OfferUiModel) -> Unit
) {
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        items(
            items = offers,
            key = { it.offerId }
        ) { offerUiModel ->
            OfferItem(
                obs = offerUiModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                onClick = {
                    onOfferClick(offerUiModel)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OffersScreenContentPreview() {
    val testOffers = listOf(
        OfferUiModel(
            offerId = "101",
            merchantLogoUrl = "https://via.placeholder.com/150.png?text=Mock+Logo+A",
            merchantName = "Mock Store A",
            distance = "1.2 km",
            shortMerchantAddress = "",
            merchantAddress = "123 Mock St",
            pointsText = "500 pts",
            offerType = 0,
            tagType = ""
        ),
        OfferUiModel(
            offerId = "102",
            merchantLogoUrl = "https://via.placeholder.com/150.png?text=Mock+Logo+A",
            merchantName = "Mock Store B",
            distance = "100.2 mi",
            shortMerchantAddress = "",
            merchantAddress = "456 Mock St",
            pointsText = "50 pts",
            offerType = 0,
            tagType = ""
        )
    )
    OffersScreenContent(Modifier,testOffers) {}
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    permissions: List<String> = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ),
    onPermissionGranted: () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    val grantedOnce = remember { mutableStateOf(false) }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        Log.d("PermissionCheck", "Permission granted: ${permissionState.allPermissionsGranted}")
        if (permissionState.allPermissionsGranted && !grantedOnce.value) {
            grantedOnce.value = true
            onPermissionGranted()
        }
    }
}

