package com.paywith.offersdemo.ui.screen.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.SearchRegion
import com.paywith.offersdemo.ui.component.AppStandardScreen
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.theme.ColorSearchHint
import com.paywith.offersdemo.ui.theme.LightBlue
import com.paywith.offersdemo.ui.theme.LightGrey

@Composable
fun SearchRegionScreen(
    appViewModel: AppViewModel,
    onBackClick: () -> Unit,
    onLocationSelected: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    val locations by appViewModel.locations

    AppStandardScreen(
        title = stringResource(R.string.where_to),
        snackbarHostState = snackbarHostState,
        onBack = onBackClick,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchRegionContent(
                modifier = Modifier.fillMaxWidth(),
                searchQuery = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    appViewModel.onSearchRegionChange(it)
                }
            )

            LocationsList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                locations = locations,
                onLocationClick = {
                    searchQuery = it.description
                    onLocationSelected(it.placeId)
                }
            )
        }
    }
}


@Composable
fun SearchRegionContent(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        // "Where To" text
        Text(
            text = stringResource(R.string.where_to),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 0.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Search input (OutlinedTextField)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.neighborhood_city_zip_code),
                    color = ColorSearchHint
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)

        )

        Spacer(modifier = Modifier.height(15.dp))

        // Current Location text with icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pointer),
                contentDescription = "Current Location",
                tint = LightBlue,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = stringResource(R.string.current_location),
                color = LightBlue,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Suggested Location Text
        Text(
            text = stringResource(R.string.suggested_location),
            color = LightGrey,
            fontSize = 12.sp
        )
    }
}


@Composable
fun LocationsList(
    modifier: Modifier = Modifier,
    locations: List<SearchRegion>,
    onLocationClick: (SearchRegion) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations) { location ->
            Text(
                text = location.description,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { onLocationClick(location) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchRegionContent() {
    SearchRegionContent(
        searchQuery = "San Francisco",
        onQueryChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationsList() {
    LocationsList(
        locations = listOf(
            SearchRegion("San Francisco, CA", "ChIJIQBpAG2ahYAR_6128GcTUEo"),
            SearchRegion("New York, NY", "ChIJOwg_06VPwokRYv534QaPC8g"),
            SearchRegion("Los Angeles, CA", "ChIJE9on3F3HwoAR9AhGJW_fL-I"),
            SearchRegion("Chicago, IL", "ChIJ7cv00DwsDogRAMDACa2m4K8")
        )
    )
}
