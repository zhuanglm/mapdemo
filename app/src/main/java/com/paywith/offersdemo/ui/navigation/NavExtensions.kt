package com.paywith.offersdemo.ui.navigation

import androidx.navigation.NavController

fun NavController.navigateAndClearStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
