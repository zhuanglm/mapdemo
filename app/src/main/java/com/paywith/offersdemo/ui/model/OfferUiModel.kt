package com.paywith.offersdemo.ui.model

import android.location.Location

data class OfferUiModel(
    val offerId: String,
    val merchantLogoUrl: String,
    val merchantName: String,
    val distance: String,
    val shortMerchantAddress: String,
    val merchantAddress: String,
    val pointsText: String,
    val offerDetail: String? = null,
    val merchantDescription: String? = null,
    val merchantPhoneNumber: String? = null,
    val merchantWebsite: String? = null,
    val onlineOrderingLink: String? = null,
    val facebookPageLink: String? = null,
    val instagramPageLink: String? = null,
    val twitterPageLink: String? = null,
    val merchantLocation: Location? = null,
    val offerType: Int,
    val tagType: String
)

