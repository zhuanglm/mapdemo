package com.paywith.offersdemo.data.repository

import com.paywith.offersdemo.data.model.ApiResponse
import com.paywith.offersdemo.data.model.CustomerDto
import com.paywith.offersdemo.data.model.CustomerWrapper
import com.paywith.offersdemo.data.network.ApiService
import com.paywith.offersdemo.data.network.parseErrorMessage
import com.paywith.offersdemo.domain.model.CustomerSignUp
import com.paywith.offersdemo.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun login(phone: String, password: String): ApiResponse<CustomerSignUp> {
        val customerSignUp = CustomerDto(mobileNumber = formatPhoneNumber(phone), password = password)

        return try {
            val response = apiService.userLogin(CustomerWrapper(customerSignUp))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResponse.Success(body.toCustomerSignUp())
                } else {
                    ApiResponse.Failure("")
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody())
                ApiResponse.Failure(" ${response.code()}: $errorMsg", HttpException(response))
            }
        } catch (e: Exception) {
            ApiResponse.Failure(e.message, e)
        }

    }

    /*private fun isPhoneValid(phone: String?): Boolean {
        if (phone != null && android.util.Patterns.PHONE.matcher(phone).matches()) {
            val p: Pattern = Pattern.compile("(1)?-[0-9]{3}-[0-9]{3}-[0-9]{4}")
            val m: Matcher = p.matcher(phone)
            return m.find() && m.group() == phone
        }
        return false
    }*/

    private fun formatPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.contains("+") and !phoneNumber.contentEquals("-")) {
            return phoneNumber
        }
        return "+${phoneNumber.replace("-", "")}"
    }

    private fun CustomerDto.toCustomerSignUp(): CustomerSignUp {
        return CustomerSignUp(
            mobileNumber = this.mobileNumber,
            password = this.password,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            verificationCode = this.verificationCode,
            passwordConfirmation = this.passwordConfirmation,
            zipCode = this.zipCode
        )
    }
}
