package com.paywith.offersdemo.ui.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.component.AppStandardScreen
import com.paywith.offersdemo.ui.getPointsText
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.model.OfferUiModel
import kotlinx.coroutines.launch
/**
 * Displays the merchant details screen.
 *
 * This composable function is responsible for rendering the UI that shows information
 * about a specific merchant and their offers. It utilizes an [AppViewModel] to fetch
 * the offer details based on the provided [offerID].
 *
 * The screen includes a back navigation action, handled by [onBackClick].
 * It uses [AppStandardScreen] as a base layout and displays [MerchantScreenContent]
 * if the offer data is available.
 *
 * @param appViewModel The view model providing access to application data,
 *                     specifically for retrieving offer details.
 * @param offerID The unique identifier of the offer to be displayed.
 * @param onBackClick A lambda function to be invoked when the back button is pressed.
 */
@Composable
fun MerchantScreen(
    appViewModel: AppViewModel,
    offerID: String,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val offer = appViewModel.getOfferById(offerID)

    AppStandardScreen(
    title = offer?.merchantName?:stringResource(R.string.merchant),
    snackbarHostState = snackbarHostState,
    onBack = onBackClick
    ) { padding ->

        offer?.let {
            MerchantScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                offer
            )
        }
    }

}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MerchantScreenContent(
    modifier: Modifier,
    offerUiModel: OfferUiModel
) {
    val bannerURL = offerUiModel.merchantLogoUrl
    val merchantAddress = offerUiModel.merchantAddress
    val offerPoints = getPointsText(offerUiModel)
    val offerDetails = offerUiModel.offerDetail.orEmpty()

    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Banner Image
        Image(
            painter = rememberAsyncImagePainter(model = bannerURL),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
               .height(screenHeightDp * 0.2f)
        )
        // Merchant Address
        Text(
            text = merchantAddress,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(start = 30.dp, top = 5.dp, end = 16.dp)
                .fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        val tabTitles = listOf(stringResource(R.string.offer), stringResource(R.string.profile))
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { tabTitles.size }
        // Tabs
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) { page ->
            when (page) {
                0 -> OfferTabContent(
                    offerPoints = offerPoints,
                    offerDetails = offerDetails
                    )
                1 -> ProfileTabContent(offerUiModel)
            }
        }
    }
}
