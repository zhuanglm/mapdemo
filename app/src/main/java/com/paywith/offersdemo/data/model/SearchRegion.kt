package com.paywith.offersdemo.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Project: Offers Demo
 * File: SearchRegion
 * Created: 2025-06-16
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

@Parcelize
data class SearchRegion(val description: String,
                        val placeId: String) : Parcelable
