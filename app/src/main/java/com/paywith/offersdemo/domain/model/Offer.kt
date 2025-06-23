package com.paywith.offersdemo.domain.model

import android.icu.text.DecimalFormat
import android.location.Location

/**
 * Represents an offer from a merchant.
 *
 * This data class holds all the information related to an offer, including details about the
 * merchant, the offer itself (acquisition or loyalty), and various links and media.
 *
 * @property id The unique identifier for the offer.
 * @property merchantId The unique identifier for the merchant.
 * @property businessName The name of the business offering the deal.
 * @property tagType The type of tag associated with the offer (e.g., "Eat", "Shop").
 */

data class Offer(
    val id: Int? = null,
    val merchantId: Int? = null,
    val businessName: String? = null,
    val tagType: String? = null,
    val phone: String? = null,
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val country: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val coordinates: String? = null,
    val description: String? = null,
    val websiteLink: String? = null,
    val facebookLink: String? = null,
    val instagramLink: String? = null,
    val twitterLink: String? = null,
    val yelpLink: String? = null,
    val merchantBanner: String? = null,
    val merchantLogo: String? = null,
    val locationBanner: String? = null,
    val locationLogo: String? = null,
    val legacyBanner: String? = null,
    val legacyLogo: String? = null,
    val locationName: String? = null,
    val acquisitionTitle: String? = null,
    val acquisitionSummary: String? = null,
    val acquisitionType: String? = null,
    val acquisitionAmount: String? = null,
    val acquisitionMinSpent: String? = null,
    val acquisitionTransactionCount: String? = null,
    val loyaltyTitle: String? = null,
    val loyaltySummary: String? = null,
    val loyaltyType: String? = null,
    val loyaltyAmount: String? = null,
    val loyaltyMinSpent: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val onlineOrderingLink: String? = null
)  {

    val isLoyalty: Boolean
        get() = isValidAmount(loyaltyAmount)

    val isAcquisition: Boolean
        get() = isValidAmount(acquisitionAmount)

    val offerType: Int
        get() = if (isAcquisition) ACQUISITION_OFFER
        else if (isLoyalty) LOYALTY_OFFER
        else DATA_ERROR

    private fun isValidAmount(s: String?): Boolean {
        return if (s == null)
            false
        else try {
            s.toDouble() > 0.0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Returns the address in the format "Address, City"
     */
    fun getBasicAddress(): String {
        return "${address1}, $city"
    }

    /**
     * Returns the address in the format "Address, City, State, Zip"
     */
    fun getFullAddress(): String {
        return "${address1}, ${city}, ${state}, $zip"
    }

    /**
     * Returns the distance in miles, truncated to one decimal point, between this offer and the given coordinates.
     */
    fun getDistanceInMiles(coordinates: Coords): String {
        return if (coordinates.latitude == SearchQuery.DEFAULT_LAT && coordinates.longitude == SearchQuery.DEFAULT_LNG) {
            "Distance Unknown"
        } else {
            val offerLocation = getLocationFromMerchantLocation(this.coordinates.toString())
            val offerCoordinates = Coords.fromLocation(offerLocation)
            val dist = coordinates.getDistance(offerCoordinates, Coords.DistanceType.MILES)
            val format = DecimalFormat("#,###.#")
            return format.format(dist) + " mi."
        }
    }

    fun getLogo(): String {
        return when {
            !locationLogo.isNullOrEmpty() -> locationLogo
            !merchantLogo.isNullOrEmpty() -> merchantLogo
            !legacyLogo.isNullOrEmpty() -> legacyLogo
            else -> ""
        }
    }

    companion object {
        const val DATA_ERROR = -1
        const val ACQUISITION_OFFER = 0
        const val LOYALTY_OFFER = 1

        fun getLocationFromMerchantLocation(merchantCoordinates: String): Location {
            val location = Location("merchant")
            val coordinates = merchantCoordinates.split(",")
            location.latitude = coordinates[0].toDouble()
            location.longitude = coordinates[1].toDouble()

            return location
        }
    }
}