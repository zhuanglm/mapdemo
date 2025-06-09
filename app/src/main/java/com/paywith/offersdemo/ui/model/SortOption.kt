package com.paywith.offersdemo.ui.model

import android.content.res.Resources
import androidx.annotation.StringRes
import com.paywith.offersdemo.R

/**
 * Project: Offers Demo
 * File: SortOption
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */

data class SortOption(
    @StringRes val labelResId: Int,
    val queryValue: String
){
    companion object {
        val all = listOf(
            SortOption(R.string.closest, "closest"),
            SortOption(R.string.best_loyalty, "loyalty_offer_amount")
        )

        fun default() = all.first()

        fun fromLabelString(label: String, resources: Resources): SortOption? {
            return all.firstOrNull {
                resources.getString(it.labelResId) == label
            }
        }
    }
}

