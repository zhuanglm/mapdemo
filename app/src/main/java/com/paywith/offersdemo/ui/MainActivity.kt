package com.paywith.offersdemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paywith.offersdemo.ui.screen.AppScaffold
import com.paywith.offersdemo.ui.theme.OffersDemoTheme
import dagger.hilt.android.AndroidEntryPoint
/**
 * Project: Offers Demo
 * File: MainActivity
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Main activity for the Offers Demo application.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OffersDemoTheme {
                AppScaffold()
            }
        }
    }
}