package com.paywith.buoylocaldemo.ui.offers

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.paywith.buoylocaldemo.ui.AppViewModel
import com.paywith.buoylocaldemo.ui.model.OfferUiModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(appViewModel: AppViewModel) {
    LocationPermissionHandler(
        onPermissionGranted = {
            appViewModel.fetchLocation()
        }
    )

    val location by appViewModel.locationFlow.collectAsState()
    val offers by appViewModel.offers.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(location) {
        location?.let {
            snackbarHostState.showSnackbar("Location: ${it.latitude}, ${it.longitude}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Offers") })
        }
    ) { padding ->
        OffersScreenContent(
            offers = offers,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun OffersScreenContent(offers: List<OfferUiModel>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = offers,
            key = { it.offerId }
        ) { offerUiModel ->
            OfferItem(
                obs = offerUiModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
            shortMerchantAddress = "",
            merchantAddress = "123 Mock St",
            pointsText = "500 points!"
        ),
        OfferUiModel(
            offerId = "102",
            merchantLogoUrl = "https://via.placeholder.com/150.png?text=Mock+Logo+A",
            merchantName = "Mock Store B",
            shortMerchantAddress = "",
            merchantAddress = "456 Mock St",
            pointsText = "50 points!"
        )
    )
    OffersScreenContent(testOffers)
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

