package com.paywith.offersdemo.ui.navigation

sealed class NavRoute(val route: String) {
    data object Login : NavRoute("login")
    data object Home : NavRoute("home")
    data object Offers : NavRoute("offers")

//    data class Detail(val id: String) : NavRoute("detail/{id}") {
//        fun createRoute(id: String) = "detail/$id"
//    }
}
