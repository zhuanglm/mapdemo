package com.paywith.offersdemo.ui.navigation

import androidx.navigation.NavController

/**
 * Navigates to the specified [route] while clearing the entire back stack.
 *
 * This means all previous destinations will be removed from the back stack,
 * effectively making the target destination the new root.
 *
 * @param route The navigation route to navigate to.
 *
 * Behavior:
 * - `popUpTo(0) { inclusive = true }`: Pops all entries in the back stack including the root.
 * - `launchSingleTop = true`: Prevents multiple copies of the same destination from being created.
 */

fun NavController.navigateAndClearStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
