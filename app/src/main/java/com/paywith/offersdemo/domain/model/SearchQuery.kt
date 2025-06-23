package com.paywith.offersdemo.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
/**
 * Represents a search query for offers.
 *
 * This data class is used to encapsulate the parameters for searching offers,
 * including filtering, sorting, location, distance, and a general query string.
 * It is Parcelable to allow it to be passed between Android components.
 *
 * @property filter The type of tag to filter by (e.g., "category", "brand"). Serialized as "tag_type".
 * @property sort The attribute to sort the results by. Serialized as "sort_by_attribute".
 * @property lat The latitude for location-based searches. Defaults to [DEFAULT_LAT].
 * @property lng The longitude for location-based searches. Defaults to [DEFAULT_LNG].
 * @property distance The search radius from the specified coordinates (e.g., "5000km").
 * @property query A general search query string.
 */
@Parcelize
data class SearchQuery(
    @SerializedName("tag_type") val filter: String = "",
    @SerializedName("sort_by_attribute") val sort: String = "",
    @SerializedName("latitude") val lat: String = DEFAULT_LAT.toString(),
    @SerializedName("longitude") val lng: String = DEFAULT_LNG.toString(),
    @SerializedName("distance") val distance: String = "5000km",
    @SerializedName("query") val query: String = ""
) : Parcelable {

    fun withCoords(coords: Coords): SearchQuery {
        return copy(
            lat = coords.latitude.toString(),
            lng = coords.longitude.toString()
        )
    }

    companion object {
        private const val CURRENT_LAT = 45.42508
        private const val CURRENT_LONG = -75.70046
        const val DEFAULT_LAT = CURRENT_LAT
        const val DEFAULT_LNG = CURRENT_LONG
    }
}
