package com.vu.s8014554Assignment2.data.model

import com.squareup.moshi.Json

/** Body of the 200 response from POST /footscray/auth.*/
data class LoginResponse(
    @param:Json(name = "keypass") val keypass: String
)
