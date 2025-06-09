package com.paywith.offersdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Project: Offers Demo
 * File: OfferTag
 * Created: 2025-06-06
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

@Parcelize
data class OfferTags(
    @SerializedName(value = "tag_types") val tagTypes: ArrayList<String>) : Parcelable
