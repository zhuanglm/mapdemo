package com.paywith.offersdemo.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paywith.offersdemo.ui.login.LoginScreen
import com.paywith.offersdemo.ui.screen.home.MapScreen
import com.paywith.offersdemo.ui.screen.home.MerchantScreen
import com.paywith.offersdemo.ui.screen.home.OffersScreen
import com.paywith.offersdemo.ui.screen.search.SearchMerchantScreen
import com.paywith.offersdemo.ui.screen.search.SearchRegionScreen
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.viewmodel.LoginViewModel
/**
 * Composable function that defines the navigation graph for the application.
 *
 * It uses a [NavHost] to manage navigation between different screens.
 *
 * @param navController The [NavHostController] used for navigation.
 * @param snackbarHostState The [SnackbarHostState] for displaying snackbars.
 * @param appViewModel The [AppViewModel] instance, providing data for various screens.
 * @param loginViewModel The [LoginViewModel] instance, handling login logic.
 */
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

        // Home screen route - contains map and various actions
        composable(NavRoute.Home.route) { backStackEntry ->
            // Create stable lambdas remembered across recompositions for navigation events
            val onItemClick = remember(navController) {
                { id: String ->
                    navController.navigate(NavRoute.Detail(id).createRoute(id))
                }
            }

            val onSearchClick = remember(navController) {
                {
                    navController.navigate(NavRoute.SearchMerchant.route)
                }
            }

            val onWhereToClick = remember(navController) {
                {
                    navController.navigate(NavRoute.SearchRegion.route)
                }
            }

            // Collect search result from back stack saved state
            LaunchedEffect(Unit) {
                val resultFlow = backStackEntry.savedStateHandle
                    .getStateFlow<String?>(NavRoute.Home.SEARCH_RESULT_KEY, null)

                resultFlow.collect { result ->
                    result?.let {
                        // When a search region is selected, update target location in ViewModel
                        appViewModel.setTargetLocation(it)
                    }

                    backStackEntry.savedStateHandle.remove<String>(NavRoute.Home.SEARCH_RESULT_KEY)
                }
            }

            MapScreen(
                appViewModel,
                onItemClick = onItemClick,
                onSearchClick = onSearchClick,
                onWhereToClick = onWhereToClick
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
            SearchMerchantScreen(
                appViewModel,
                onItemClick = { id ->
                    navController.navigate(NavRoute.Detail(id).createRoute(id))
                },
                onBackClick = { navController.popBackStack() })
        }

        composable(NavRoute.SearchRegion.route) {
            SearchRegionScreen(
                appViewModel,
                onBackClick = { navController.popBackStack() },
                onLocationSelected = {
                    // Save selected location into previous back stack entry (Home) state handle
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(NavRoute.Home.SEARCH_RESULT_KEY, it)

                    navController.popBackStack()
                })
        }
    }
}
