package com.paywith.offersdemo.ui.navigation

sealed class NavRoute(val route: String) {
    object Login : NavRoute("login")
    object Home : NavRoute("home")
    object Offers : NavRoute("offers")

//    data class Detail(val id: String) : NavRoute("detail/{id}") {
//        fun createRoute(id: String) = "detail/$id"
//    }
}
