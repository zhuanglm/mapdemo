package com.paywith.offersdemo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.home.MapScreen
import com.paywith.offersdemo.ui.login.LoginScreen
import com.paywith.offersdemo.ui.home.MerchantScreen
import com.paywith.offersdemo.ui.offers.OffersScreen
import com.paywith.offersdemo.ui.search.SearchMerchantScreen
import com.paywith.offersdemo.ui.search.SearchRegionScreen
import com.paywith.offersdemo.ui.viewmodel.LoginViewModel

@Composable
fun AppNavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState,
                appViewModel: AppViewModel, loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Login.route
    ) {
        composable(NavRoute.Login.route) {
            LoginScreen(loginViewModel,
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
                },
                onSearchClick = {
                    navController.navigate(NavRoute.SearchMerchant.route)
                },
                onWhereToClick = {
                    navController.navigate(NavRoute.SearchRegion.route)
                }
            )
        }

        composable(
            route = NavRoute.Detail("{id}").route,
            arguments = listOf(navArgument("id") {type = NavType.StringType})
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("id") ?: return@composable
            MerchantScreen(appViewModel = appViewModel, offerID = offerId,
                onBackClick = {navController.popBackStack()})
        }

        composable(NavRoute.SearchMerchant.route) {
            SearchMerchantScreen(appViewModel, onBackClick = {navController.popBackStack()})
        }

        composable(NavRoute.SearchRegion.route) {
            SearchRegionScreen(appViewModel, onBackClick = {navController.popBackStack()})
        }
    }
}
