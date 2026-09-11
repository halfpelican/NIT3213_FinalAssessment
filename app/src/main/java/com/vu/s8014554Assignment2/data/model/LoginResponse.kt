package com.vu.s8014554Assignment2.data.model

import com.squareup.moshi.Json

// 200 response received from server
data class LoginResponse(
    @Json(name = "keypass") val keypass: String
)
