package com.vu.s8014554Assignment2.data.remote

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds and holds the Retrofit client used to talk to the nit3213api.
 *
 * Wires together OkHttp (HTTP transport + logging), Moshi (JSON <-> Kotlin
 * conversion) and Retrofit, and exposes a ready-to-use [ApiService].
 */
class Nit3213RetrofitClient {
    /** Root of the API. Must end with "/" so Retrofit can append endpoint paths. */
    private val BASE_URL = "https://nit3213apinew.onrender.com/"

    /**
     * Logs full request and response bodies to Logcat.
     * Useful for debugging API calls during development.
     */
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * HTTP client that performs the network calls.
     *
     * Timeouts are raised from OkHttp's 10-second default because the API is
     * hosted on Render's free tier, which sleeps when idle and can take up to
     * a minute to respond to the first request.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * JSON parser. KotlinJsonAdapterFactory uses reflection to map JSON keys
     * onto Kotlin data class properties (no code generation required).
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /** Retrofit instance combining the base URL, JSON converter and HTTP client. */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    /** Retrofit-generated implementation of [ApiService], used by the repository. */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}