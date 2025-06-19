package com.paywith.offersdemo.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.data.model.OfferTags
import com.paywith.offersdemo.data.model.SearchRegion
import com.paywith.offersdemo.domain.model.Coords
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchModifier
import com.paywith.offersdemo.domain.model.SearchModifier.Filter.Companion.DEFAULT_FILTER_QUERY
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.model.SearchQuery.Companion.DEFAULT_LAT
import com.paywith.offersdemo.domain.model.SearchQuery.Companion.DEFAULT_LNG
import com.paywith.offersdemo.domain.repository.LocationRepository
import com.paywith.offersdemo.domain.usecase.FilterOffersUseCase
import com.paywith.offersdemo.domain.usecase.GetOfferTagsUseCase
import com.paywith.offersdemo.domain.usecase.GetOffersUseCase
import com.paywith.offersdemo.ui.model.LatLngWithZoom
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.model.PointsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * ViewModel for the main application screen, responsible for fetching and managing offer data,
 * handling user location, and providing data to the UI.
 *
 * This ViewModel uses Hilt for dependency injection to get instances of use cases and repositories.
 * It exposes data through [StateFlow] and [State] objects to be observed by the UI.
 *
 * Key responsibilities:
 * - Fetching the user's current location.
 * - Loading offers based on the user's location and search queries.
 * - Loading offer tags for filtering.
 * - Handling search and filtering of offers.
 * - Providing offer details to the UI.
 * - Managing the state of API responses (Loading, Success, Error).
 *
 * @property getOffers Use case for fetching offers from the repository.
 * @property getOfferTags Use case for fetching offer tags.
 * @property filterOffersUseCase Use case for filtering offers based on criteria.
 * @property location Repository for accessing location data.
 */

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getOffers: GetOffersUseCase,
    private val getOfferTags: GetOfferTagsUseCase,
    private val filterOffersUseCase: FilterOffersUseCase,
    private val location: LocationRepository
) : BaseViewModel() {

    private var searchQuery = SearchQuery(DEFAULT_FILTER_QUERY, SearchModifier.Sort.DEFAULT_SORT_QUERY)

    private val _location = MutableStateFlow<Coords?>(null)
    val locationFlow: StateFlow<Coords?> = _location

    init {
        _location
            .filterNotNull()
            .distinctUntilChanged()
            .onEach {
                loadOffers()
                loadOfferTags()
            }
            .launchIn(viewModelScope)
    }

    fun fetchLocation() {
        viewModelScope.launch {
            val loc = location.getCurrentLocation()

            val coordinates = Coords(
                latitude = loc?.latitude ?: DEFAULT_LAT,
                longitude = loc?.longitude ?: DEFAULT_LNG
            )
            if (_location.value != coordinates) {
                _location.value = coordinates
            }
            searchQuery.withCoords(coordinates)
        }
    }

    private val _locations = mutableStateOf<List<SearchRegion>>(emptyList())
    val locations: State<List<SearchRegion>> = _locations

    fun onSearchRegionChange(query: String) {
        viewModelScope.launch {
            _locations.value = location.searchRegions(query)
        }
    }

    private val _targetLocation = mutableStateOf<LatLngWithZoom?>(null)
    val targetLocation: State<LatLngWithZoom?> = _targetLocation

    fun setTargetLocation(placeID: String) {
        viewModelScope.launch {
            val latLng = location.fetchLatLngFromPlaceId(placeID)
            latLng?.let { setTargetLocation(it) }
        }
    }

    fun setTargetLocation(latLng: LatLng?, zoom: Float = 12f) {
        _targetLocation.value = latLng?.let {
            LatLngWithZoom(it, zoom)
        }
    }

    private val _offers = MutableStateFlow<ApiResponse<List<OfferUiModel>>>(ApiResponse.Loading)
    val offers: StateFlow<ApiResponse<List<OfferUiModel>>> = _offers
    private var allOffers = listOf<Offer>()

    fun loadOffers(query: String? = null) {
        viewModelScope.launch {
            val userCoordinates: Coords? = _location.value

            val search = if (query?.isNotBlank() == true) {
                searchQuery.copy(query = query)
            } else {
                searchQuery
            }

            emitMappedApiResponse(
                flow = _offers,
                sourceCall = { getOffers(search) },
                mapper = { offers ->
                    if (query.isNullOrBlank())      //cache all offers
                        allOffers = offers
                    val uiModels = offers.map { it.toOfferUiModel(userCoordinates) }
                    updateTargetLocationToFirstOffer(uiModels)
                    uiModels
                }
            )
        }
    }

    fun getOfferById(offerId: String): OfferUiModel? {
        val currentOffers = (_offers.value as? ApiResponse.Success)?.data.orEmpty()
        return currentOffers.find { it.offerId == offerId }
    }

    fun updateSort(sort: String) {
        searchQuery = searchQuery.copy(sort = sort)
        loadOffers()
    }

    fun updateFilter(filter: String) {
        val filteredOffers = filterOffersUseCase(allOffers, filter)
            .map { it.toOfferUiModel(null) }
        updateTargetLocationToFirstOffer(filteredOffers)
        _offers.value = ApiResponse.Success(filteredOffers)
    }

    fun searchOffersByQuery(query: String) {
        loadOffers(query)
    }

    fun restoreAllOffers() {
        val userLocation = _location.value
        val restoredOffers = allOffers.map { it.toOfferUiModel(userLocation) }
        _offers.value = ApiResponse.Success(restoredOffers)
    }


    private fun updateTargetLocationToFirstOffer(offers: List<OfferUiModel>) {
        if (offers.isNotEmpty()) {
            val firstLoc = offers[0].merchantLocation
            if (firstLoc != null) {
                setTargetLocation(LatLng(firstLoc.latitude, firstLoc.longitude), zoom = 12f)
            }
        }
    }


    private val _offerTags = MutableStateFlow<ApiResponse<OfferTags>>(ApiResponse.Loading)

    private fun loadOfferTags() {
        viewModelScope.launch {
            emitApiResponse(
                flow = _offerTags,
                sourceCall = { getOfferTags() }
            )
        }
    }

    fun getFilterOptions(): List<String> {
        val currentTags = (_offerTags.value as? ApiResponse.Success)?.data?.tagTypes.orEmpty()
        return currentTags
    }

    private fun getValue(value: String?): Int {
        return if (value == null || value == "null") {
            0
        } else {
            value.toDouble().roundToInt()
        }
    }

    private fun Offer.toOfferUiModel(userLocation: Coords?): OfferUiModel {
        val (amount, type) = when {
            isAcquisition -> getValue(acquisitionAmount) to PointsType.ACQUISITION
            isLoyalty -> getValue(loyaltyAmount) to PointsType.LOYALTY
            else -> 0 to PointsType.NONE
        }

        val distance = if (userLocation != null) {
            this.getDistanceInMiles(userLocation)
        } else {
            "Distance Unknown"
        }
        val shortMerchantAddress = this.getBasicAddress()
        val merchantAddress = this.getFullAddress()

        return OfferUiModel(
            merchantName = this.locationName ?: "",
            merchantAddress = merchantAddress,
            pointsAmount = amount,
            pointsType = type,
            offerId = this.id?.toString() ?: UUID.randomUUID().toString(),
            offerType = this.offerType,
            tagType = this.tagType ?: "",
            merchantLogoUrl = this.getLogo(),
            shortMerchantAddress = shortMerchantAddress,
            distance = distance,
            offerDetail = this.acquisitionSummary,
            merchantLocation = this.coordinates?.let { Offer.getLocationFromMerchantLocation(it)},
            merchantDescription = this.description,
            merchantWebsite = this.websiteLink,
            merchantPhoneNumber = this.phone,
            facebookPageLink = this.facebookLink,
            instagramPageLink = this.instagramLink,
            twitterPageLink = this.twitterLink,
        )
    }
}