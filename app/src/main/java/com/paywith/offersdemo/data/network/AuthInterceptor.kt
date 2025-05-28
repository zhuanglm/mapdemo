package com.paywith.offersdemo.data.network

import okhttp3.Interceptor
import okhttp3.Response

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
