package com.paywith.offersdemo.ui.model

import android.location.Location
/**
 * Data class representing the UI model for an offer.
 *
 * This class encapsulates all the information needed to display an offer in the user interface.
 *
 * @property offerId Unique identifier for the offer.
 * @property merchantLogoUrl URL for the merchant's logo.
 * @property merchantName Name of the merchant.
 * @property distance Formatted string representing the distance to the merchant.
 * @property shortMerchantAddress A concise version of the merchant's address.
 * @property merchantAddress The full address of the merchant.
 * @property pointsAmount The number of points associated with the offer.
 * @property pointsType The type of points (e.g., Acquisition, Loyalty).
 * @property offerDetail Optional detailed description of the offer.
 * @property merchantDescription Optional description of the merchant.
 * @property merchantPhoneNumber Optional phone number of the merchant.
 * @property merchantWebsite Optional website URL of the merchant.
 * @property onlineOrderingLink Optional link for online ordering.
 * @property facebookPageLink Optional link to the merchant's Facebook page.
 * @property instagramPageLink Optional link to the merchant's Instagram page.
 * @property twitterPageLink Optional link to the merchant's Twitter page.
 * @property merchantLocation Optional [Location] object representing the merchant's geographical location.
 * @property offerType Integer representing the type of the offer.
 * @property tagType String representing the tag type associated with the offer.
 */
data class OfferUiModel(
    val offerId: String,
    val merchantLogoUrl: String,
    val merchantName: String,
    val distance: String,
    val shortMerchantAddress: String,
    val merchantAddress: String,
    val pointsAmount: Int,
    val pointsType: PointsType,
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

enum class PointsType {
    ACQUISITION,
    LOYALTY,
    NONE
}
