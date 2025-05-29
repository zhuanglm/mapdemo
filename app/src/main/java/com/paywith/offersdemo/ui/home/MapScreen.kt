package com.paywith.offersdemo.ui.home

import android.location.Location
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
import com.paywith.offersdemo.ui.offers.OffersScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(appViewModel: AppViewModel) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val selectedMerchant = remember { mutableStateOf<Merchant?>(null) }
    val showSortDialog = remember { mutableStateOf(false) }
    val showFilterDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

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
                    OffersBottomSheet(offers)
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
                    GoogleMapView(
                        loc,
                        onMarkerClick = { merchant ->
                            selectedMerchant.value = merchant
                        }
                    )
                }

                selectedMerchant.value?.let { merchant ->
                    MerchantMapBox(merchant)
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
                    rightButtonText = stringResource(R.string.where_to))
            }
        }
    }
}

// ---- 以下是占位 Composables，可以后续逐步实现 ----
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
fun GoogleMapView(location: Location, onMarkerClick: (Merchant) -> Unit) {
    val latLng = LatLng(location.latitude, location.longitude)

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(latLng, 12f)
        },
        onMapClick = {
            // Handle general map click
        }
    ) {
        Marker(
            state = MarkerState(position = latLng),
            title = "Singapore",
            snippet = "Marker in Singapore",
            onClick = {
                onMarkerClick(Merchant("Singapore"))
                true
            }
        )
    }
}


@Composable
fun OffersBottomSheet(
    offers: List<OfferUiModel>,
    onSortClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
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
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun MerchantMapBox(merchant: Merchant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text("Selected Merchant: ${merchant.name}")
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

// ---- 数据模型占位 ----
data class Merchant(val name: String)
