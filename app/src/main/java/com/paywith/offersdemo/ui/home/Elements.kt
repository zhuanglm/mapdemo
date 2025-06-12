package com.paywith.offersdemo.ui.home

/**
 * Project: Offers Demo
 * Company: paywith.com
 * File: elements.kt
 * Created: 2025-06-06
 * Developer: Ray Z
 *
 * Description:
 * This file defines reusable UI components used throughout the Offers Demo app, including:
 * - `ButtonsRow`: A configurable row of two elevated buttons, each with optional icons and click actions.
 * - `SelectableOptionGroup`: A vertical list of radio-button-style options used for sort and filter selection.
 * - `MerchantMapBox`: A box-style offer display composable used on the map screen.
 *
 * These components follow Jetpack Compose patterns and support flexibility in layout and styling.
 *
 * All rights reserved © paywith.com.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.offers.OfferItem


@Composable
fun ButtonsRow(
    modifier: Modifier = Modifier,
    leftButtonText: String,
    rightButtonText: String,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    onLeftClick: () -> Unit = { },
    onRightClick: () -> Unit = { },
    roundedCorner: Dp = 5.dp
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val buttonHeight = 40.dp
        ElevatedButton(
            onClick = onLeftClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
            shape = RoundedCornerShape(roundedCorner),
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leftIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = leftButtonText,
                    maxLines = 1
                )
            }
        }

        ElevatedButton(
            onClick = onRightClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
            shape = RoundedCornerShape(roundedCorner),
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                rightIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = rightButtonText,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchButtonsRowPreview() {
    ButtonsRow(
        leftButtonText = stringResource(R.string.search_merchants),
        rightButtonText = stringResource(R.string.where_to),
    )
}

@Preview(showBackground = true)
@Composable
fun FielterButtonsRowPreview() {
    ButtonsRow(
        leftIcon = ImageVector.vectorResource(R.drawable.ic_sort),
        rightIcon = ImageVector.vectorResource(R.drawable.ic_filter_eat),
        leftButtonText = stringResource(R.string.closest),
        rightButtonText = stringResource(R.string.all),
        roundedCorner = 30.dp
    )
}

@Composable
fun SelectableOptionGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = {
                            if (option != selectedOption) {
                                onOptionSelected(option)
                            }
                        }
                    )
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = null // handled by Row
                )
                Text(
                    text = option, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    }
}


@Preview(showBackground = true)
@Composable
fun SelectableOptionGroupPreview() {
    val closestText = stringResource(R.string.closest)
    var selectedOption by remember { mutableStateOf(closestText) }

    val options = listOf(
        stringResource(R.string.closest),
        stringResource(R.string.best_loyalty)
    )

    MaterialTheme {
        Surface {
            SelectableOptionGroup(
                title = "Sort By",
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it }
            )
        }
    }
}

@Composable
fun MerchantMapBox(
    modifier: Modifier = Modifier,
    offer: OfferUiModel,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 0.dp, bottom = 16.dp, start = 32.dp, end = 32.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    ) {
        OfferItem(
            obs = offer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            onClick = { onClick(offer.offerId) }
        )
    }
}