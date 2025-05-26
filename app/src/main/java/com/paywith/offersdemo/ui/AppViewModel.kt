package com.paywith.offersdemo.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywith.offersdemo.domain.model.Coords
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.model.SearchModifier
import com.paywith.offersdemo.domain.repository.LocationRepository
import com.paywith.offersdemo.domain.usecase.GetOffersUseCase
import com.paywith.offersdemo.ui.model.OfferUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val getOffers: GetOffersUseCase,
    private val location: LocationRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun showLoading() {
        _loading.value = true
    }

    fun hideLoading() {
        _loading.value = false
    }

    val DEFAULT_FILTER_QUERY: String = "All"
    private val searchQuery = SearchQuery(DEFAULT_FILTER_QUERY, SearchModifier.Sort.DEFAULT_SORT_QUERY)

    private val _offers = MutableStateFlow<List<OfferUiModel>>(emptyList())
    val offers: StateFlow<List<OfferUiModel>> = _offers
    private val _location = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _location

    init {
        _location
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { availableLocation ->
                Log.d("AppViewModel", "Location available, triggering offers load.")
                loadMockOffers()
                //loadOffers()
            }
            .launchIn(viewModelScope)
    }

    fun fetchLocation() {
        viewModelScope.launch {
            //showLoading()
            val loc = location.getCurrentLocation()
            _location.value = loc
            Log.d("LocationDebug", "Location: lat=${loc?.latitude}, lng=${loc?.longitude}")

            loc?.let { Coords.fromLocation(it) }?.let { searchQuery.setCoords(it) }
            //hideLoading()
        }
    }

    private fun loadOffers() {
        viewModelScope.launch {
            showLoading()

            try {
                val userLocation = _location.value
                val userCoords: Coords? = userLocation?.let { Coords.fromLocation(it) }

                val offersResult: Result<List<Offer>> = getOffers(searchQuery)
                offersResult
                    .onSuccess { domainOffers ->
                        val uiOffers: List<OfferUiModel> = withContext(Dispatchers.Default) {
                            domainOffers.map { it.toOfferUiModel(userCoords) }
                        }
                        _offers.value = uiOffers
                    }
                    .onFailure { throwable ->
                        Log.e("AppViewModel", "Failed to load offers: ${throwable.message}", throwable)
                        _offers.value = emptyList()
                    }
            }finally {
                hideLoading()
            }
        }
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
            _offers.value = uiOffers
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

    fun Offer.toOfferUiModel(userLocation: Coords?): OfferUiModel {
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

        val shortMerchantAddress = if (userLocation != null) {
            this.getDistanceInMiles(userLocation)
        } else {
            "Distance Unknown"
        }
        val merchantAddress = shortMerchantAddress + " \u2022 " + this.getBasicAddress()

        return OfferUiModel(
            merchantName = this.locationName ?: "",
            merchantAddress = merchantAddress,
            pointsText = pointsText,
            offerId = this.id?.toString() ?: UUID.randomUUID().toString(),
            merchantLogoUrl = this.getLogo(),
            shortMerchantAddress = shortMerchantAddress,
        )
    }
}