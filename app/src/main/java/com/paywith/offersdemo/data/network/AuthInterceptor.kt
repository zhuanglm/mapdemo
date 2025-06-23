package com.paywith.offersdemo.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for adding authentication headers to network requests.
 *
 * This interceptor adds the "JWT_AUD" header with a value of "buoylocal" to all requests.
 * If an authentication token is available, it also adds the "Authorization" header with the token.
 * After receiving a response, it checks for an "Authorization" header in the response.
 * If a new token is found in the response header, it updates the stored authentication token.
 */
class AuthInterceptor : Interceptor {

    private var authToken: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader("JWT_AUD", "buoylocal")

        if (authToken.isNotBlank()) {
            requestBuilder.addHeader("Authorization", authToken)
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        response.header("Authorization")?.let { header ->
            if (header != authToken) {
                authToken = header
            }
        }

        return response
    }
}
