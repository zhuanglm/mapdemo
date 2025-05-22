package com.paywith.buoylocaldemo.ui.model

import androidx.compose.ui.graphics.painter.Painter

data class OfferUiModel(
    val offerId: String,
    val merchantLogoUrl: String,
    val merchantName: String,
    val shortMerchantAddress: String,
    val merchantAddress: String,
    val pointsText: String
)

