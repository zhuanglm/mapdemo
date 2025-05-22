package com.paywith.buoylocaldemo.domain.model

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
         * Generic coords for Bangor, Maine
         */
        private const val BANGOR_LAT = 44.808147
        private const val BANGOR_LONG = -68.795013
        const val DEFAULT_LAT = BANGOR_LAT
        const val DEFAULT_LNG = BANGOR_LONG
    }
}