package com.paywith.offersdemo.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.rememberNavController
import com.paywith.offersdemo.ui.navigation.AppNavGraph
import com.paywith.offersdemo.ui.theme.OffersDemoTheme
import com.paywith.offersdemo.ui.viewmodel.AppViewModel
import com.paywith.offersdemo.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isLoading by appViewModel.loading.collectAsState(initial = false)
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(snackbarHostState) {
                appViewModel.errorMessage.collectLatest { message ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = androidx.compose.material3.SnackbarDuration.Long
                    )
                }
            }

            val navController = rememberNavController()

            OffersDemoTheme {
                AppBackground(modifier = Modifier.fillMaxWidth()) {
                    AppNavGraph(navController, snackbarHostState, appViewModel, loginViewModel)
                }

                if (isLoading) {
                    Log.d("MainActivity", "loading...")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit){}
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
