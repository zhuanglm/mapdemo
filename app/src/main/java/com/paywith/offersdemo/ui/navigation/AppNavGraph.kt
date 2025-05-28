package com.paywith.offersdemo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paywith.offersdemo.ui.AppViewModel
import com.paywith.offersdemo.ui.login.LoginScreen
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
                navController.navigateAndClearStack(NavRoute.Offers.route)
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
