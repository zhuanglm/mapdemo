package com.paywith.offersdemo.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paywith.offersdemo.R
import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.ui.AppViewModel

@Composable
fun LoginScreen(
    appViewModel: AppViewModel, snackbarHostState: SnackbarHostState,
    onLoginSuccess: () -> Unit
) {
    var phone by remember { mutableStateOf("12565768172") }
    var password by remember { mutableStateOf("Testing1!") }
    val loginState by appViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is ApiResponse.Success) {
            onLoginSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LoginScreenContent(
                phone = phone,
                password = password,
                onPhoneChange = { phone = it },
                onPasswordChange = { password = it },
                onLoginClick = {
                    appViewModel.userLogin(phone, password)
                }
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    phone: String? = null,
    password: String? = null,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Image(
            painter = painterResource(id = R.drawable.shoppingbag),
            contentDescription = "Shopping Bag",
            modifier = Modifier.size(256.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = phone ?: "",
                    onValueChange = onPhoneChange,
                    label = { Text(stringResource(R.string.mobile_number)) },
                    placeholder = { Text("Enter mobile number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password ?: "",
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    placeholder = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.forgot_password),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp),
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .defaultMinSize(minWidth = 180.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        onPhoneChange = {},
        onPasswordChange = {}
    ) { }
}
