package com.paywith.offersdemo.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchQuery(
    @SerializedName("tag_type") var filter: String = "",
    @SerializedName("sort_by_attribute") var sort: String = "",
    @SerializedName("latitude") var lat: String = DEFAULT_LAT.toString(),
    @SerializedName("longitude") var lng: String = DEFAULT_LNG.toString(),
    @SerializedName("distance") val distance: String = "5000km",
    @SerializedName("query") var query: String = "") : Parcelable {

    constructor(filter: String, sort: String) : this() {
        this.filter = filter
        this.sort = sort
    }

    constructor(searchQuery: SearchQuery) : this(searchQuery.filter,
        searchQuery.sort,
        searchQuery.lat,
        searchQuery.lng,
        searchQuery.distance,
        searchQuery.query
    )

    fun setCoords(coords: Coords) {
        lat = coords.latitude.toString()
        lng = coords.longitude.toString()
    }

    companion object {
        /**
         * Generic coords for your location
         */
        private const val CURRENT_LAT = 45.42508
        private const val CURRENT_LONG = -75.70046
        const val DEFAULT_LAT = CURRENT_LAT
        const val DEFAULT_LNG = CURRENT_LONG
    }
}