package com.paywith.offersdemo.ui

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
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.model.PointsType


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
 * @param color .
 * @return Marker.icon BitmapDescriptor.
 */
@Composable
fun rememberMarkerIconFromVector(
    @DrawableRes drawableResId: Int
): BitmapDescriptor {
    val context = LocalContext.current

    return remember(drawableResId) {

        val drawable = ContextCompat.getDrawable(context, drawableResId)
            ?: throw IllegalArgumentException("Resource not found")

        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

@Composable
fun getOfferMarkerIcon(offer: OfferUiModel, isSelected: Boolean): BitmapDescriptor {
    val resId = when (offer.tagType) {
        "Eat" -> if (isSelected) R.drawable.ic_marker_restaurant_selected else R.drawable.ic_marker_restaurant
        "Shop" -> if (isSelected) R.drawable.ic_marker_shop_selected else R.drawable.ic_marker_shop
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
