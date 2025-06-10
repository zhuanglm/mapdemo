package com.paywith.offersdemo.ui

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.data.model.OfferTags
import com.paywith.offersdemo.domain.model.Coords
import com.paywith.offersdemo.domain.model.CustomerSignUp
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchModifier
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.model.SearchQuery.Companion.DEFAULT_LAT
import com.paywith.offersdemo.domain.model.SearchQuery.Companion.DEFAULT_LNG
import com.paywith.offersdemo.domain.repository.LocationRepository
import com.paywith.offersdemo.domain.usecase.GetOffersUseCase
import com.paywith.offersdemo.domain.usecase.GetOfferTagsUseCase
import com.paywith.offersdemo.domain.usecase.LoginUseCase
import com.paywith.offersdemo.ui.model.OfferUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val getOffers: GetOffersUseCase,
    private val getOfferTags: GetOfferTagsUseCase,
    private val location: LocationRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    private fun showLoading() {
        _loading.value = true
    }

    private fun hideLoading() {
        _loading.value = false
    }

    private val DEFAULT_FILTER_QUERY: String = "All"
    private val searchQuery = SearchQuery(DEFAULT_FILTER_QUERY, SearchModifier.Sort.DEFAULT_SORT_QUERY)

    private val _location = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _location

    init {
        _location
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { availableLocation ->
                Log.d("AppViewModel", "Location available, triggering offers load.")
                //loadMockOffers()
                loadOffers()
                loadOfferTags()
            }
            .launchIn(viewModelScope)
    }

    fun onResume() {
        fetchLocation()
    }

    fun fetchLocation() {
        viewModelScope.launch {
            val loc = location.getCurrentLocation()
            Log.d("LocationDebug", "Location: lat=${loc?.latitude}, lng=${loc?.longitude}")

            val finalLocation = loc ?: Location("").apply {
                latitude = DEFAULT_LAT
                longitude = DEFAULT_LNG
            }

            _location.value = finalLocation
            searchQuery.setCoords(Coords.fromLocation(finalLocation))
        }
    }

    private val _locations = mutableStateOf<List<String>>(emptyList())
    val locations: State<List<String>> = _locations

    fun onSearchRegionChange(query: String) {
        viewModelScope.launch {
            _locations.value = location.searchRegions(query)
            Log.d("SearchDebug", "Locations: $locations")
        }
    }

    private suspend fun <T, R> emitMappedApiResponse(
        flow: MutableStateFlow<ApiResponse<R>>,
        sourceCall: suspend () -> ApiResponse<T>,
        mapper: suspend (T) -> R
    ) {
        showLoading()
        flow.value = ApiResponse.Loading
        try {
            when (val result = sourceCall()) {
                is ApiResponse.Success -> {
                    val mapped = withContext(Dispatchers.Default) { mapper(result.data) }
                    flow.value = ApiResponse.Success(mapped)
                }
                is ApiResponse.Failure -> {
                    flow.value = ApiResponse.Failure(result.message, result.exception)
                    _errorMessage.emit(result.message ?: "Unknown error")
                }
                is ApiResponse.Loading -> {
                    flow.value = ApiResponse.Loading
                }
            }
        } finally {
            hideLoading()
        }
    }

    private suspend fun <T> emitApiResponse(
        flow: MutableStateFlow<ApiResponse<T>>,
        sourceCall: suspend () -> ApiResponse<T>
    ) {
        showLoading()
        flow.value = ApiResponse.Loading
        try {
            when (val result = sourceCall()) {
                is ApiResponse.Success -> {
                    flow.value = result
                }
                is ApiResponse.Failure -> {
                    flow.value = result
                    _errorMessage.emit(result.message ?: "Unknown error")
                }
                is ApiResponse.Loading -> {
                    flow.value = ApiResponse.Loading
                }
            }
        } finally {
            hideLoading()
        }
    }


    private val _offers = MutableStateFlow<ApiResponse<List<OfferUiModel>>>(ApiResponse.Loading)
    val offers: StateFlow<ApiResponse<List<OfferUiModel>>> = _offers

    fun loadOffers(query: String? = null) {
        viewModelScope.launch {
            val userLocation = _location.value
            val userCoords: Coords? = userLocation?.let { Coords.fromLocation(it) }

            val search = if (query?.isNotBlank() == true) {
                searchQuery.copy(query = query)
            } else {
                searchQuery
            }

            emitMappedApiResponse(
                flow = _offers,
                sourceCall = { getOffers(search) },
                mapper = { offers ->
                    offers.map { it.toOfferUiModel(userCoords) }
                }
            )
        }
    }

    fun getOfferById(offerId: String): OfferUiModel? {
        val currentOffers = (_offers.value as? ApiResponse.Success)?.data.orEmpty()
        return currentOffers.find { it.offerId == offerId }
    }

    fun getOffers(): List<OfferUiModel> {
        val currentOffers = (_offers.value as? ApiResponse.Success)?.data.orEmpty()
        return currentOffers
    }

    fun updateSort(sort: String) {
        searchQuery.sort = sort
        loadOffers()
    }

    fun updateFilter(filter: String) {
        searchQuery.filter = filter
        loadOffers()
    }

    fun searchOffersByQuery(query: String) {
        loadOffers(query)
    }

    private val _loginState = MutableStateFlow<ApiResponse<CustomerSignUp>>(ApiResponse.Loading)
    val loginState: StateFlow<ApiResponse<CustomerSignUp>> =_loginState

    fun userLogin(phone: String, password: String) {

        viewModelScope.launch {
            emitApiResponse(
                flow = _loginState,
                sourceCall = { login(phone, password) }
            )
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

    private fun loadMockOffers() {
        viewModelScope.launch {
            showLoading()
            val uiOffers = withContext(Dispatchers.Default) {
                val userLocation = _location.value
                val userCoords: Coords? = userLocation?.let { Coords.fromLocation(it) }

                delay(1000L)
                val mockOffers = listOf(
                    Offer(
                        id = 1,
                        merchantId = 101,
                        businessName = "Mock Store A",
                        tagType = "Retail",
                        phone = "555-1111",
                        address1 = "123 Mock St",
                        address2 = null,
                        city = "Mockville",
                        country = "CA",
                        state = "ON",
                        zip = "M0K 0K0",
                        coordinates = "45.4215, -75.6972", // Ottawa coordinates
                        description = "A great mock store!",
                        websiteLink = null, facebookLink = null, instagramLink = null, twitterLink = null, yelpLink = null,
                        merchantBanner = "https://via.placeholder.com/600x200.png?text=Mock+Banner+A",
                        merchantLogo = "https://via.placeholder.com/150.png?text=Mock+Logo+A",
                        locationBanner = null, locationLogo = null, legacyBanner = null, legacyLogo = null,
                        locationName = "Store A - Mockville",
                        acquisitionTitle = "Get 500 points!", acquisitionSummary = null, acquisitionType = null, acquisitionMinSpent = null, acquisitionTransactionCount = null,
                        createdAt = "2024-01-01T10:00:00Z", updatedAt = "2024-01-01T10:00:00Z", onlineOrderingLink = null
                    ),
                    Offer(
                        id = 2,
                        merchantId = 102,
                        businessName = "Mock Cafe B",
                        tagType = "Cafe",
                        phone = "555-2222",
                        address1 = "456 Mock Rd",
                        address2 = null,
                        city = "Mockville",
                        country = "CA",
                        state = "ON",
                        zip = "M0K 0K0",
                        coordinates = "45.4250, -75.7000",
                        description = "Delicious mock coffee!",
                        websiteLink = null, facebookLink = null, instagramLink = null, twitterLink = null, yelpLink = null,
                        merchantBanner = null, merchantLogo = null, locationBanner = null, locationLogo = "https://via.placeholder.com/150.png?text=Mock+Logo+B", legacyBanner = null, legacyLogo = null,
                        locationName = "Cafe B - Mockville",
                        loyaltyTitle = "Earn 5x points!", loyaltySummary = null, loyaltyType = null, loyaltyMinSpent = null,
                        createdAt = "2024-01-02T11:00:00Z", updatedAt = "2024-01-02T11:00:00Z", onlineOrderingLink = null,
                    )
                )
                mockOffers.map { it.toOfferUiModel(userCoords) }
            }
            _offers.value = ApiResponse.Success(uiOffers)
            hideLoading()
        }
    }

    private fun getValue(value: String?): Int {
        return if (value == null || value == "null") {
            0
        } else {
            value.toDouble().toInt()
        }
    }

    private fun Offer.toOfferUiModel(userLocation: Coords?): OfferUiModel {
        val pointsText = if (this.isAcquisition) {
            if (getValue(this.acquisitionAmount) > 1) {
                "${getValue(this.acquisitionAmount)} pts"
            } else {
                "${getValue(this.acquisitionAmount)} pt"
            }
        } else if (this.isLoyalty) {
            if (getValue(this.loyaltyAmount) > 1) {
                "${getValue(this.loyaltyAmount)}x pts"
            } else {
                "${getValue(this.loyaltyAmount)}x pt"
            }
        } else {
            "0 pts"
        }

        val distangce = if (userLocation != null) {
            this.getDistanceInMiles(userLocation)
        } else {
            "Distance Unknown"
        }
        val shortMerchantAddress = this.getBasicAddress()
        val merchantAddress = this.getFullAddress()

        return OfferUiModel(
            merchantName = this.locationName ?: "",
            merchantAddress = merchantAddress,
            pointsText = pointsText,
            offerId = this.id?.toString() ?: UUID.randomUUID().toString(),
            offerType = this.offerType,
            tagType = this.tagType ?: "",
            merchantLogoUrl = this.getLogo(),
            shortMerchantAddress = shortMerchantAddress,
            distance = distangce,
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