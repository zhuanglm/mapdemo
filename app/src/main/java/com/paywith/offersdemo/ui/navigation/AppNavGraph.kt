package com.paywith.offersdemo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.home.MapScreen
import com.paywith.offersdemo.ui.login.LoginScreen
import com.paywith.offersdemo.ui.offers.MerchantScreen
import com.paywith.offersdemo.ui.offers.OffersScreen

@Composable
fun AppNavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState,
                appViewModel: AppViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Login.route
    ) {
        composable(NavRoute.Login.route) {
            LoginScreen(appViewModel,
                snackbarHostState,
                onLoginSuccess = {
                navController.navigateAndClearStack(NavRoute.Home.route)
            })
        }

        composable(NavRoute.Offers.route) {
            OffersScreen(appViewModel)
        }

        composable(NavRoute.Home.route) {
            MapScreen(
                appViewModel,
                onItemClick = { id ->
                    navController.navigate(NavRoute.Detail(id).createRoute(id))
                })
        }

        composable(
            route = NavRoute.Detail("{id}").route,
            arguments = listOf(navArgument("id") {type = NavType.StringType})
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("id") ?: return@composable
            MerchantScreen(appViewModel = appViewModel, offerID = offerId,
                onBackClick = {navController.popBackStack()})
        }

    }
}
