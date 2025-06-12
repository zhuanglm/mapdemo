package com.paywith.offersdemo.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
