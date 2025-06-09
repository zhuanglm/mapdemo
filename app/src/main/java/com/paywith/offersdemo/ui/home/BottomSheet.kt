package com.paywith.offersdemo.ui.home

/**
 * Project: Offers Demo
 * Company: paywith.com
 * File: BottomSheet.kt
 * Created: 2025-06-04
 * Developer: Ray Z
 *
 * Description:
 * This file defines the BottomSheet UI component, including logic for displaying
 * selectable sort and filter options with dynamic state-driven expansion and collapse.
 * Built using Jetpack Compose with a focus on reactive UI updates.
 *
 * All rights reserved © paywith.com.
 */

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.offers.OffersScreenContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ExpandedSection {
    NONE, SORT, FILTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    offers: List<OfferUiModel>,
    filterOptions: List<String>,
    onSortClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onItemClick: (String) -> Unit
) {
    val sortOptions = listOf(stringResource(R.string.closest), stringResource(R.string.best_loyalty))

    val allText = stringResource(R.string.all)
    val fullFilterOptions = remember(filterOptions) {
        listOf(allText) + filterOptions
    }

    var selectedSort by remember { mutableStateOf(sortOptions.firstOrNull() ?: "") }
    var selectedFilter by remember { mutableStateOf(allText) }
    var expandedSection by remember { mutableStateOf(ExpandedSection.NONE) }
    var restoreToPartial by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ButtonsRow(
            leftIcon = ImageVector.vectorResource(R.drawable.ic_sort),
            rightIcon = ImageVector.vectorResource(R.drawable.ic_filter_eat),
            leftButtonText = selectedSort,
            rightButtonText = selectedFilter,
            onLeftClick = {
                handleSectionClick(
                    targetSection = ExpandedSection.SORT,
                    currentExpanded = expandedSection,
                    scaffoldState = scaffoldState,
                    coroutineScope = coroutineScope,
                    restoreToPartial = restoreToPartial,
                    onRestoreChange = { restoreToPartial = it },
                    onExpandedSectionChange = { expandedSection = it }
                )
            },
            onRightClick = {
                handleSectionClick(
                    targetSection = ExpandedSection.FILTER,
                    currentExpanded = expandedSection,
                    scaffoldState = scaffoldState,
                    coroutineScope = coroutineScope,
                    restoreToPartial = restoreToPartial,
                    onRestoreChange = { restoreToPartial = it },
                    onExpandedSectionChange = { expandedSection = it }
                )
            },
            roundedCorner = 30.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        when (expandedSection) {
            ExpandedSection.SORT -> {
                SelectableOptionGroupWrapper(
                    titleResId = R.string.sort_by,
                    options = sortOptions,
                    selectedOption = selectedSort,
                    onOptionSelected = { selectedSort = it },
                    restoreToPartial = restoreToPartial,
                    onRestoreChange = { restoreToPartial = it },
                    scaffoldState = scaffoldState,
                    coroutineScope = coroutineScope,
                    onClose = { expandedSection = ExpandedSection.NONE }
                )
            }
            ExpandedSection.FILTER -> {
                SelectableOptionGroupWrapper(
                    titleResId = R.string.filter_by,
                    options = fullFilterOptions,
                    selectedOption = selectedFilter,
                    onOptionSelected = {
                        selectedFilter = it
                        onFilterClick()
                                       },
                    restoreToPartial = restoreToPartial,
                    onRestoreChange = { restoreToPartial = it },
                    scaffoldState = scaffoldState,
                    coroutineScope = coroutineScope,
                    onClose = { expandedSection = ExpandedSection.NONE }
                )
            }
            ExpandedSection.NONE -> {}
        }



        Spacer(modifier = Modifier.height(8.dp))

        OffersScreenContent(
            offers = offers,
            modifier = Modifier.weight(1f),
            onOfferClick = { offer ->
                onItemClick(offer.offerId)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun handleSectionClick(
    targetSection: ExpandedSection,
    currentExpanded: ExpandedSection,
    scaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    restoreToPartial: Boolean,
    onRestoreChange: (Boolean) -> Unit,
    onExpandedSectionChange: (ExpandedSection) -> Unit
) {
    val shouldRestore = shouldRestoreToPartial(
        currentSheetState = scaffoldState.bottomSheetState.currentValue,
        wasRestoreToPartial = restoreToPartial
    )

    onRestoreChange(shouldRestore)

    coroutineScope.launch {
        if (shouldRestore) {
            scaffoldState.bottomSheetState.expand()
        }
    }

    onExpandedSectionChange(
        if (currentExpanded == targetSection) ExpandedSection.NONE else targetSection
    )
}

/**
 * Determines whether we should restore to PartiallyExpanded state after selection.
 *
 * If already in expanded state, and user clicks again,
 * we remember whether it was expanded from partially.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun shouldRestoreToPartial(
    currentSheetState: SheetValue,
    wasRestoreToPartial: Boolean
): Boolean {
    return if (wasRestoreToPartial && currentSheetState != SheetValue.PartiallyExpanded) {
        true
    } else {
        currentSheetState == SheetValue.PartiallyExpanded
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableOptionGroupWrapper(
    @StringRes titleResId: Int,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    restoreToPartial: Boolean,
    onRestoreChange: (Boolean) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    onClose: () -> Unit
) {
    SelectableOptionGroup(
        title = stringResource(titleResId),
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = {
            onOptionSelected(it)
            onClose()

            if (restoreToPartial) {
                onRestoreChange(false)
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }
    )
}

