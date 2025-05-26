package com.paywith.offersdemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.login.LoginScreen
import com.paywith.offersdemo.ui.offers.OffersScreen
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavGraph(navController: NavHostController, appViewModel: AppViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Login.route
    ) {
        composable(NavRoute.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(NavRoute.Offers.route) {
                    popUpTo(NavRoute.Login.route) { inclusive = true }
                }
            })
        }

        composable(NavRoute.Offers.route) {
            OffersScreen(appViewModel)
        }
        /*composable(NavRoute.Home.route) {
            HomeScreen(onItemClick = { id ->
                navController.navigate(NavRoute.Detail(id).createRoute(id))
            })
        }

        composable(
            route = NavRoute.Detail("{id}").route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("id") ?: ""
            DetailScreen(id = id)
        }*/
    }
}
