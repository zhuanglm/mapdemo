package com.paywith.offersdemo.ui.navigation

import androidx.navigation.NavController

class NavExtensions {
    fun NavController.navigateAndClearStack(route: String) {
        navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }

}