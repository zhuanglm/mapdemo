package com.paywith.offersdemo.ui.model

data class OfferUiModel(
    val offerId: String,
    val merchantLogoUrl: String,
    val merchantName: String,
    val distance: String,
    val shortMerchantAddress: String,
    val merchantAddress: String,
    val pointsText: String
)

