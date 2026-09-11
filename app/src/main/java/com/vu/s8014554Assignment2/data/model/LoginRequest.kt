package com.vu.s8014554Assignment2.data.model

import com.squareup.moshi.Json

// Body sent to POST /footscray/auth.
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    )
