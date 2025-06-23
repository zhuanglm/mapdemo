package com.paywith.offersdemo

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
/**
 * Project: Offers Demo
 * File: OffersDemoApp.kt
 * Created: 2025-06-09
 * Developer: Ray Z
 * Description: Main application class for the Offers Demo app. Initializes Hilt for dependency injection
 * and the Google Places SDK.
 *
 * This file is part of a Jetpack Compose-based Kotlin application.
 * All rights reserved © paywith.com.
 */
@HiltAndroidApp
class OffersDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //Google Places SDK
        if (!Places.isInitialized()) {
            val mapsApiKey = getMetaDataValue(this)
            mapsApiKey?.let {
                Places.initialize(
                    applicationContext,
                    it
                )
            }
        }
    }

    /**
     * Retrieves the string value of a specified meta-data tag from the AndroidManifest.xml.
     *
     * @param context The context object.
     * @return The corresponding string value, or null if not found or an error occurs.
     */
    private fun getMetaDataValue(context: Context): String? {
        //The value of the android:name attribute in the meta-data tag.
        val metaDataName = "com.google.android.geo.API_KEY"

        try {
            val applicationInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA // this flag is required to retrieve meta-data
            )
            val bundle: Bundle? = applicationInfo.metaData
            return bundle?.getString(metaDataName)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("MetaDataHelper", "Failed to get package info for ${context.packageName}: ${e.message}")
            return null
        } catch (e: Exception) {
            Log.e("MetaDataHelper", "Failed to get meta-data value for $metaDataName: ${e.message}")
            return null
        }
    }
}
