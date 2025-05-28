package com.paywith.offersdemo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CustomerDto(
    @SerializedName("id") val id: UUID? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("mobile_number") val mobileNumber: String? = null,
    @SerializedName("verification_code") val verificationCode: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("password_confirmation") val passwordConfirmation: String? = null,
    @SerializedName("zip_code") val zipCode: String? = null
) : Parcelable