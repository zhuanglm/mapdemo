package com.paywith.offersdemo.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.AppStandardScreen
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.offers.OffersScreenContent
import com.paywith.offersdemo.ui.theme.ColorSearchHint
import com.paywith.offersdemo.ui.theme.LightBlue


@Composable
fun SearchMerchantScreen(
    appViewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    val offersState by appViewModel.offers.collectAsState()
    val offers = (offersState as? ApiResponse.Success)?.data

    AppStandardScreen(
        title = stringResource(R.string.merchant),
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchMerchantContent(
                modifier = Modifier
                    .fillMaxWidth(),
                    //.wrapContentHeight(),
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearchClick = {
                    appViewModel.searchOffersByQuery(searchQuery)
                },
                onCancelClick = {
                    //clear search query and load offers
                    appViewModel.loadOffers()
                    onBackClick()
                }
            )

            offers?.let {
                OffersScreenContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    offers = it,
                    onOfferClick = { }
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchMerchantScreenPreview() {
    val query by remember { mutableStateOf("") }

    SearchMerchantContent(searchQuery = query, onQueryChange = {})
}

@Composable
fun SearchMerchantContent(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    searchQuery: String,
    onQueryChange: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(horizontal = dimensionResource(id = R.dimen.margin_twenty))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.margin_twenty)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.cancel),
                color = LightBlue,
                style = MaterialTheme.typography.bodyLarge, // Assuming mapped from @style/SearchOffer
                modifier = Modifier.clickable { onCancelClick() }
            )
            Text(
                text = stringResource(id = R.string.search),
                color = LightBlue,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable { onSearchClick() }
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_twenty)))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text(
                    color = ColorSearchHint,
                    text = stringResource(id = R.string.search)
                ) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_thirty)))
    }
}