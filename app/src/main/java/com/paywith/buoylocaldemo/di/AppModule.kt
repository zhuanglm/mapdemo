package com.paywith.buoylocaldemo.di

import com.paywith.buoylocaldemo.data.location.LocationProvider
import com.paywith.buoylocaldemo.data.remote.ApiService
import com.paywith.buoylocaldemo.data.repository.OffersRepoImpl
import com.paywith.buoylocaldemo.data.repository.LocationRepoImpl
import com.paywith.buoylocaldemo.domain.repository.LocationRepository
import com.paywith.buoylocaldemo.domain.repository.OffersRepository
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
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
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
    fun provideLocationRepository(
        locationProvider: LocationProvider
    ): LocationRepository = LocationRepoImpl(locationProvider)
}
