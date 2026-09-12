package com.vu.s8014554Assignment2.data.repository

import com.vu.s8014554Assignment2.data.model.DashboardResponse
import com.vu.s8014554Assignment2.data.remote.ApiService
import javax.inject.Inject

/**
 * Single source of dashboard data for the app.
 *
 * Sits between the ViewModel and the network layer so the UI never deals with
 * Retrofit directly. Takes [ApiService] through its constructor so a fake can
 * be supplied in unit tests and Hilt can inject the real one.
 */
class DashboardRepository @Inject constructor(private val apiService: ApiService) {

    /**
     * Fetches the entities for a topic.
     *
     * Exceptions are deliberately not caught here — they propagate to the
     * ViewModel, which decides what error message the user sees.
     *
     * @param keypass Topic key from the login response, e.g. "art".
     * @return The [DashboardResponse] containing the entity list and total.
     * @throws retrofit2.HttpException on a non-2xx response (404 = unknown keypass).
     * @throws java.io.IOException on network failure or timeout.
     */
    suspend fun getEntities(keypass: String): DashboardResponse =
        apiService.getDashboard(keypass)
}