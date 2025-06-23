package com.paywith.offersdemo.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.paywith.offersdemo.ui.theme.endColor
/**
 * A standard screen layout for the application.
 *
 * This composable function provides a consistent structure for screens,
 * including a top app bar with an optional back button, a snackbar host,
 * and a floating action button for navigation.
 *
 * @param modifier The modifier to be applied to the screen.
 * @param title The title to be displayed in the top app bar.
 * @param snackbarHostState The state for managing snackbars.
 * @param onBack An optional lambda to be invoked when the back button is pressed.
 *               If null, the back button and floating action button will not be displayed.
 * @param content The main content of the screen, which receives padding values to adjust for system bars.
 */
// Base screen layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStandardScreen(
    modifier: Modifier = Modifier,
    title: String,
    snackbarHostState: SnackbarHostState,
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },

        floatingActionButton = {
            if (onBack != null) {
                FloatingActionButton(
                    onClick = onBack,
                    containerColor = endColor,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Preview(showBackground = true)
@Composable
fun AppStandardScreenPreview() {
    val snackbarHostState = SnackbarHostState()
    AppStandardScreen(
        title = "Preview",
        snackbarHostState = snackbarHostState,
        onBack = { }
    ) { }
}