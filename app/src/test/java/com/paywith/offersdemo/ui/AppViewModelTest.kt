package com.paywith.offersdemo.ui

import android.location.Location
import app.cash.turbine.test
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.domain.model.Offer
import com.paywith.offersdemo.domain.repository.LocationRepository
import com.paywith.offersdemo.domain.usecase.GetOfferTagsUseCase
import com.paywith.offersdemo.domain.usecase.GetOffersUseCase
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AppViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AppViewModel
    private lateinit var getOffers: GetOffersUseCase
    private lateinit var getOfferTags: GetOfferTagsUseCase
    private lateinit var locationRepository: LocationRepository

    // Mock data used across tests
    private val mockLocation: Location = mockk()

    @Before
    fun setup() {
        getOffers = mockk()
        getOfferTags = mockk(relaxed = true)
        locationRepository = mockk()

        // Now, define the behavior of your mockLocation
        every { mockLocation.latitude } returns 40.7128
        every { mockLocation.longitude } returns -74.0060

        // Crucial: Mock the location call that happens on init
        coEvery { locationRepository.getCurrentLocation() } returns mockLocation

        viewModel = AppViewModel(
            getOffers = getOffers,
            getOfferTags = getOfferTags,
            location = locationRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadOffers emits mapped OfferUiModel on success`(): Unit = runTest {
        // Arrange
        val offer = Offer(
            id = 1,
            locationName = "Test Shop",
            acquisitionAmount = "10.0",
            loyaltyAmount = null,
            tagType = "Food",
            acquisitionSummary = "Get 10 pts",
            description = "Best offer",
            websiteLink = null,
            phone = null,
            facebookLink = null,
            instagramLink = null,
            twitterLink = null,
            address1 = "123 Street",
            address2 = null,
            city = "City",
            merchantLogo = null
        )

        coEvery { getOffers(any()) } returns ApiResponse.Success(listOf(offer))

        // Act & Assert
        viewModel.offers.test {
            viewModel.loadOffers()

            var result = awaitItem()
            while (result is ApiResponse.Loading) {
                result = awaitItem()
            }
            assert(result is ApiResponse.Success)
            val data = (result as ApiResponse.Success).data
            assertEquals(1, data.size)
            assertEquals("Test Shop", data[0].merchantName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Use case returns empty list`(): Unit = runTest {
        // Arrange: mock use case returning empty list
        coEvery { getOffers(any()) } returns ApiResponse.Success(emptyList())

        // Act: call loadOffers
        viewModel.loadOffers()

        // Assert: offers StateFlow emits empty list ApiResponse.Success(emptyList())
        val result = viewModel.offers.first { it is ApiResponse.Success }

        assertTrue(result is ApiResponse.Success)
        val data = (result as ApiResponse.Success).data
        assertTrue(data.isEmpty())
    }


    @Test
    fun `Use case throws exception`() {
        // Verify that `offers` StateFlow remains as an empty list (or its previous state if applicable) and the exception is handled gracefully (e.g., logged, or a specific error state is set if the ViewModel had error handling) when `GetOffersUseCase` throws an exception.
        // TODO implement test
    }

    @Test
    fun `ViewModel cleared during offer loading`() = runTest {
        val delayTime = 1000L

        // Mock the use case, suspend for a period of time
        coEvery { getOffers(any()) } coAnswers {
            delay(delayTime)
            ApiResponse.Success(emptyList())
        }

        // Access the private _offers field via reflection
        val offersField = AppViewModel::class.java.getDeclaredField("_offers").apply {
            isAccessible = true
        }
        val offersFlow = offersField.get(viewModel) as MutableStateFlow<*>

        // Launch the loadOffers coroutine
        val job = launch {
            viewModel.loadOffers()
        }

        // Let the coroutine run until before the delay call (coroutine has started executing)
        runCurrent()

        // Cancel the job, simulating ViewModel clearing
        job.cancel()

        // Wait for the coroutine to be fully canceled
        job.join()

        // _offers should still be Loading (because the coroutine was canceled, success was not emitted)
        // or Failure if cancellation is handled by emitting a failure state.
        val currentValue = offersFlow.value
        assert(currentValue is ApiResponse.Loading || currentValue is ApiResponse.Failure) {
            "Expected _offers not updated after cancellation, but was $currentValue"
        }

        // Verify that the use case's getOffers method **was called** (here it's allowed to have been called but not completed)
        coVerify(atLeast = 1) { getOffers(any()) }
    }



    @Test
    fun `Multiple calls to loadOffers update offers StateFlow correctly`() = runTest {
        // Arrange: 2 Offer lists
        val offer1 = Offer(
            id = 1,
            locationName = "Shop One",
            acquisitionAmount = "5.0",
            loyaltyAmount = null,
            tagType = "Food",
            acquisitionSummary = "5 pts",
            description = "Desc 1",
            websiteLink = null,
            phone = null,
            facebookLink = null,
            instagramLink = null,
            twitterLink = null,
            address1 = "Address 1",
            address2 = null,
            city = "City",
            merchantLogo = null
        )

        val offer2 = Offer(
            id = 2,
            locationName = "Shop Two",
            acquisitionAmount = "10.0",
            loyaltyAmount = null,
            tagType = "Drink",
            acquisitionSummary = "10 pts",
            description = "Desc 2",
            websiteLink = null,
            phone = null,
            facebookLink = null,
            instagramLink = null,
            twitterLink = null,
            address1 = "Address 2",
            address2 = null,
            city = "City",
            merchantLogo = null
        )

        // Mock getOffers 2 calls returning different lists
        coEvery { getOffers(any()) } returnsMany listOf(
            ApiResponse.Success(listOf(offer1)),
            ApiResponse.Success(listOf(offer2))
        )

        // test offers StateFlow
        viewModel.offers.test {
            // Act: 1st call
            viewModel.loadOffers()
            var firstResult = awaitItem()
            while (firstResult is ApiResponse.Loading) {
                firstResult = awaitItem()
            }
            assert(firstResult is ApiResponse.Success)
            val firstData = (firstResult as ApiResponse.Success).data
            assertEquals(1, firstData.size)
            assertEquals("Shop One", firstData[0].merchantName)

            // Act: 2nd call
            viewModel.loadOffers()
            var secondResult = awaitItem()
            while (secondResult is ApiResponse.Loading) {
                secondResult = awaitItem()
            }
            assert(secondResult is ApiResponse.Success)
            val secondData = (secondResult as ApiResponse.Success).data
            assertEquals(1, secondData.size)
            assertEquals("Shop Two", secondData[0].merchantName)

            cancelAndIgnoreRemainingEvents()
        }

        // Verify that the use case's getOffers method was called twice
        coVerify(exactly = 2) { getOffers(any()) }
    }


    @Test
    fun `Offers with various data`() {
        // Test with `Offer` objects containing different valid data types, nulls (if nullable), empty strings, and potentially very large data to ensure no serialization/deserialization or display issues downstream, although this is more of an integration concern for `GetOffersUseCase` itself.
        // TODO implement test
    }

    @Test
    fun `Concurrency  Multiple observers`() {
        // Verify that multiple collectors of the `offers` StateFlow all receive the same, correct updates when offers are loaded.
        // TODO implement test
    }

    @Test
    fun `StateFlow replay behavior`() {
        // Verify that a new collector subscribing to `offers` after `loadOffers` has completed immediately receives the most recently emitted list of offers.
        // TODO implement test
    }

    @Test
    fun `Dependency  GetOffersUseCase  mock verification`() {
        // Ensure that the `getOffers()` method on the mocked `GetOffersUseCase` is called exactly once during the `init` block.
        // TODO implement test
    }

}