package com.paywith.offersdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerWrapper(
    @SerializedName("customer") val customerSignUp: CustomerDto?
) : Parcelable