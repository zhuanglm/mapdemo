package com.paywith.offersdemo.ui.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.AppStandardScreen
import com.paywith.offersdemo.ui.AppViewModel

@Composable
fun MerchantScreen(
    appViewModel: AppViewModel,
    offerID: String,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar("offerid: $offerID")
    }

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
                bannerURL = it.merchantLogoUrl,
                merchantName = offer.merchantName,
                merchantAddress = offer.merchantAddress
            )
        }
    }

}

@Composable
fun MerchantScreenContent(modifier: Modifier, bannerURL: String, merchantName: String, merchantAddress: String) {
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
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        // Tabs
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        // Pager
        val pagerState = rememberPagerState { tabTitles.size }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OfferTabContent()   // 替换为你的 offer fragment 内容
                1 -> ProfileTabContent() // 替换为你的 profile fragment 内容
            }
        }
    }
}

@Composable
fun OfferTabContent() {

}

@Composable
fun ProfileTabContent() {

}