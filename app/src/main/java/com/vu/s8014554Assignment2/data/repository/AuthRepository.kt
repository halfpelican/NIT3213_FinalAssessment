package com.vu.s8014554Assignment2.data.repository

import com.vu.s8014554Assignment2.data.model.LoginRequest
import com.vu.s8014554Assignment2.data.model.LoginResponse
import com.vu.s8014554Assignment2.data.remote.ApiService
import javax.inject.Inject

/**
 * Single source of authentication data for the app.
 *
 * Sits between the ViewModel and the network layer so the UI never deals
 * with Retrofit or request models directly. Takes [ApiService] through its
 * constructor so a fake can be supplied in unit tests and Hilt can inject the real one.
 */
class AuthRepository @Inject constructor(private val apiService: ApiService) {
    /**
     * Sends the student's credentials to the Footscray auth endpoint.
     *
     * Exceptions are deliberately not caught here — they propagate to the
     * ViewModel, which decides what error message the user sees.
     *
     * @param username Student ID without the leading "s", e.g. "8014554".
     * @param password Student first name (case-sensitive).
     * @return The [LoginResponse] containing the keypass for the dashboard call.
     * @throws retrofit2.HttpException on a non-2xx response (400 = wrong credentials).
     * @throws java.io.IOException on network failure or timeout.
     */
    suspend fun login(username: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(username = username, password = password))
    }
}