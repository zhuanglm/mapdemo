package com.paywith.offersdemo.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.model.PointsType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp



/**
 * Project: Offers Demo
 * File: UiUtils
 * Created: 2025-06-10
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

/**
 * convert Vector Drawable to Map Marker BitmapDescriptor。
 *
 * @param drawableResId Vector Drawable res ID (for example: R.drawable.ic_map_marker_default).
 * @return Marker.icon BitmapDescriptor.
 */
@Composable
fun rememberMarkerIconFromVector(
    @DrawableRes drawableResId: Int,
): BitmapDescriptor {
    val context = LocalContext.current
    val density = LocalDensity.current
    val targetHeightDp: Dp = 48.dp

    return remember(drawableResId, targetHeightDp) {
        val drawable = ContextCompat.getDrawable(context, drawableResId)
            ?: throw IllegalArgumentException("Drawable resource not found: $drawableResId")

        val originalWidth = drawable.intrinsicWidth
        val originalHeight = drawable.intrinsicHeight

        val safeHeight = if (originalHeight > 0) originalHeight else 1
        val safeWidth = if (originalWidth > 0) originalWidth else 1

        val aspectRatio = safeWidth.toFloat() / safeHeight

        // dp -> px
        val targetHeightPx = with(density) { targetHeightDp.toPx() }.toInt()
        val scaledWidthPx = (targetHeightPx * aspectRatio).toInt()

        val bitmap = createBitmap(scaledWidthPx, targetHeightPx)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}



@Composable
fun getOfferMarkerIcon(offer: OfferUiModel, isSelected: Boolean): BitmapDescriptor {
    val resId = when (offer.tagType) {
        "Eat" -> if (isSelected) R.drawable.marker_dining_select else R.drawable.marker_dining_select
        "Shop" -> if (isSelected) R.drawable.marker_shopping_select else R.drawable.marker_shopping
        "Service" -> if (isSelected) R.drawable.marker_services_select else R.drawable.marker_services
        else -> if (isSelected) R.drawable.ic_map_marker_selected else R.drawable.ic_map_marker_default
    }

    return rememberMarkerIconFromVector(resId)
}

@Composable
fun getPointsText(offer: OfferUiModel): String {
    return when (offer.pointsType) {
        PointsType.ACQUISITION ->
            pluralStringResource(R.plurals.points, offer.pointsAmount, offer.pointsAmount)
        PointsType.LOYALTY ->
            pluralStringResource(R.plurals.loyalty_points, offer.pointsAmount, offer.pointsAmount)
        PointsType.NONE ->
            stringResource(R.string.zero_points)
    }
}

/**
 * A Composable function that handles location permission requests.
 *
 * It requests the specified location permissions when the composable enters the composition.
 * Once all requested permissions are granted, it invokes the `onPermissionGranted` callback.
 * This callback is only invoked once, even if the permission state changes subsequently (e.g., if permissions are revoked and then granted again).
 *
 * @param permissions A list of location permission strings to request. Defaults to `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`.
 * @param onPermissionGranted A lambda function to be executed when all requested permissions are granted for the first time.
 */
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
