package com.paywith.offersdemo.data.network

import okhttp3.ResponseBody
import org.json.JSONArray

fun parseErrorMessage(errorBody: ResponseBody?): String {
    return try {
        val errorJson = errorBody?.string()
        val errorArray = JSONArray(errorJson)
        val firstObj = errorArray.optJSONObject(0)
        firstObj?.optString("text") ?: "Unknown error"
    } catch (e: Exception) {
        "Error parsing error response"
    }
}
