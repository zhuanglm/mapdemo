package com.paywith.offersdemo.ui

import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel

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
