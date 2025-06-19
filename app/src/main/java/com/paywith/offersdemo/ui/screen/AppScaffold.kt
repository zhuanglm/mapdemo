package com.paywith.offersdemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.paywith.offersdemo.ui.component.AppBackground
import com.paywith.offersdemo.ui.navigation.AppNavGraph
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Project: Offers Demo
 * File: AppScaffold
 * Created: 2025-06-17
 * Developer: Ray Z
 * Description: [Add a brief description of the purpose of this file]
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */


@Composable
fun AppScaffold(
    appViewModel: AppViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val isLoading by appViewModel.loading.collectAsState(initial = false)
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    LaunchedEffect(snackbarHostState) {
        appViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = androidx.compose.material3.SnackbarDuration.Long
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(modifier = Modifier.fillMaxWidth()) {
            AppNavGraph(navController, snackbarHostState, appViewModel, loginViewModel)
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {}
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}