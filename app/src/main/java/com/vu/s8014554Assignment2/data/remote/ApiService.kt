package com.vu.s8014554Assignment2.data.remote

import com.vu.s8014554Assignment2.data.model.LoginRequest
import com.vu.s8014554Assignment2.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** Retrofit definition of the nit3213api endpoints. */
interface ApiService {
    @POST("footscray/auth")
    /** Authenticates the student. Returns the keypass on 200;
     * throws HttpException on 400 (wrong ID or first name). */
    suspend fun login(@Body request: LoginRequest): LoginResponse
}