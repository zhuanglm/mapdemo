package com.paywith.offersdemo.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.paywith.offersdemo.data.location.LocationProvider
import com.paywith.offersdemo.data.network.ApiService
import com.paywith.offersdemo.data.network.AuthInterceptor
import com.paywith.offersdemo.data.repository.AuthRepoImpl
import com.paywith.offersdemo.data.repository.OffersRepoImpl
import com.paywith.offersdemo.data.repository.LocationRepoImpl
import com.paywith.offersdemo.domain.repository.AuthRepository
import com.paywith.offersdemo.domain.repository.LocationRepository
import com.paywith.offersdemo.domain.repository.OffersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val STAGING_URL = "https://staging.mrewards.us.paywith.io/api/mobile/"
    private const val TEST_URL = "https://test.mrewards.us.paywith.io/api/mobile/"
    private const val BASE_URL = TEST_URL

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideOffersRepository(apiService: ApiService): OffersRepository =
        OffersRepoImpl(apiService)

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository =
        AuthRepoImpl(apiService)

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationProvider: LocationProvider
    ): LocationRepository = LocationRepoImpl(locationProvider)
}
