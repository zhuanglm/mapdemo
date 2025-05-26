package com.paywith.offersdemo.data.repository

import com.paywith.offersdemo.data.model.OfferDto
import com.paywith.offersdemo.data.remote.ApiService
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.model.SearchQuery
import com.paywith.offersdemo.domain.repository.OffersRepository
import retrofit2.HttpException
import javax.inject.Inject

class OffersRepoImpl @Inject constructor(
    private val apiService: ApiService
) : OffersRepository {
    override suspend fun getOffersByQuery(query: SearchQuery): Result<List<Offer>> {
        return try {
            val response = apiService.getOffersByQuery(query)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toOffer() } ?: emptyList())
            } else {
                Result.failure(HttpException(response))
                //throw Exception("Error fetching offers: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    fun OfferDto.toOffer(): Offer {
        return Offer(
            id = this.id,
            merchantId = this.merchantId,
            businessName = this.businessName,
            tagType = this.tagType,
            phone = this.phone,
            address1 = this.address1,
            address2 = this.address2,
            city = this.city,
            country = this.country,
            state = this.state,
            zip = this.zip,
            coordinates = this.coordinates,
            description = this.description,
            websiteLink = this.websiteLink,
            facebookLink = this.facebookLink,
            instagramLink = this.instagramLink,
            twitterLink = this.twitterLink,
            yelpLink = this.yelpLink,
            merchantBanner = this.merchantBanner,
            merchantLogo = this.merchantLogo,
            locationBanner = this.locationBanner,
            locationLogo = this.locationLogo,
            legacyBanner = this.legacyBanner,
            legacyLogo = this.legacyLogo,
            locationName = this.locationName,
            acquisitionTitle = this.acquisitionTitle,
            acquisitionSummary = this.acquisitionSummary,
            acquisitionType = this.acquisitionType,
            acquisitionAmount = this.acquisitionAmount,
            acquisitionMinSpent = this.acquisitionMinSpent,
            acquisitionTransactionCount = this.acquisitionTransactionCount,
            loyaltyTitle = this.loyaltyTitle,
            loyaltySummary = this.loyaltySummary,
            loyaltyType = this.loyaltyType,
            loyaltyAmount = this.loyaltyAmount,
            loyaltyMinSpent = this.loyaltyMinSpent,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            onlineOrderingLink = this.onlineOrderingLink
        )
    }
}