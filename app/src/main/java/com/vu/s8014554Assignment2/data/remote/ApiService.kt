package com.vu.s8014554Assignment2.data.remote

import com.vu.s8014554Assignment2.data.model.DashboardResponse
import com.vu.s8014554Assignment2.data.model.LoginRequest
import com.vu.s8014554Assignment2.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit definition of the nit3213api endpoints. */
interface ApiService {
    /** Authenticates the student. Returns the keypass on 200;
     * throws HttpException on 400 (wrong ID or first name). */
    @POST("footscray/auth")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Fetches the entities for a topic. The keypass comes from the login response.
     *
     * @throws HttpException on a non-2xx response (404 = unknown keypass).
     */
    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponse
}