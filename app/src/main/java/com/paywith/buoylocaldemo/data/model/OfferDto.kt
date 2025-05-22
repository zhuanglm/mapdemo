package com.paywith.buoylocaldemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// data/model/OfferDto.kt
@Parcelize
data class OfferDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("merchant_id") val merchantId: Int? = null,
    @SerializedName("business_name") val businessName: String? = null,
    @SerializedName("tag_type") val tagType: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address_1") val address1: String? = null,
    @SerializedName("address_2") val address2: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("zip") val zip: String? = null,
    @SerializedName("coordinates") val coordinates: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("website_link") val websiteLink: String? = null,
    @SerializedName("facebook_link") val facebookLink: String? = null,
    @SerializedName("instagram_link") val instagramLink: String? = null,
    @SerializedName("twitter_link") val twitterLink: String? = null,
    @SerializedName("yelp_link") val yelpLink: String? = null,
    @SerializedName("merchant_banner") val merchantBanner: String? = null,
    @SerializedName("merchant_logo") val merchantLogo: String? = null,
    @SerializedName("location_banner") val locationBanner: String? = null,
    @SerializedName("location_logo") val locationLogo: String? = null,
    @SerializedName("legacy_banner") val legacyBanner: String? = null,
    @SerializedName("legacy_logo") val legacyLogo: String? = null,
    @SerializedName("location_name") val locationName: String? = null,
    @SerializedName("acquisition_offer_title") val acquisitionTitle: String? = null,
    @SerializedName("acquisition_offer_summary") val acquisitionSummary: String? = null,
    @SerializedName("acquisition_offer_type") val acquisitionType: String? = null,
    @SerializedName("acquisition_offer_amount") val acquisitionAmount: String? = null,
    @SerializedName("acquisition_offer_restriction_minimum_spent") val acquisitionMinSpent: String? = null,
    @SerializedName("acquisition_offer_restriction_transaction_count") val acquisitionTransactionCount: String? = null,
    @SerializedName("loyalty_offer_title") val loyaltyTitle: String? = null,
    @SerializedName("loyalty_offer_summary") val loyaltySummary: String? = null,
    @SerializedName("loyalty_offer_type") val loyaltyType: String? = null,
    @SerializedName("loyalty_offer_amount") val loyaltyAmount: String? = null,
    @SerializedName("loyalty_offer_restriction_minimum_spent") val loyaltyMinSpent: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("online_ordering_link") val onlineOrderingLink: String? = null
) : Parcelable
