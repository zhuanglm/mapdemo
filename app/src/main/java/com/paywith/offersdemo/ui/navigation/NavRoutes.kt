package com.paywith.offersdemo.ui.navigation
/**
 * Project: Offers Demo
 * File: NavRoutes
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Defines the navigation routes for the application.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
sealed class NavRoute(val route: String) {
    data object Login : NavRoute("login")
    data object Home : NavRoute("home") {
        const val SEARCH_RESULT_KEY = "search_result_key"
    }
    data object Offers : NavRoute("offers")

    data class Detail(val id: String) : NavRoute("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }

    data object SearchMerchant : NavRoute("search_merchant")
    data object SearchRegion : NavRoute("search_region")
}
