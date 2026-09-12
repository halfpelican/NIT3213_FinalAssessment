package com.vu.s8014554Assignment2.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vu.s8014554Assignment2.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that builds the networking stack for the nit3213api.
 *
 * Installed in [SingletonComponent], so every provider here is created once
 * and shared for the lifetime of the application. Hilt resolves the graph from
 * the parameter types: asking for a [Retrofit] causes the [OkHttpClient] and
 * [Moshi] providers to run first.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Root of the API. Must end with "/" so Retrofit can append endpoint paths. */
    private const val BASE_URL = "https://nit3213apinew.onrender.com/"

    /**
     * Logs full request and response bodies to Logcat.
     * Useful for debugging API calls during development.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * HTTP client that performs the network calls.
     *
     * A single instance is shared so its connection and thread pools are reused.
     * Timeouts are raised from OkHttp's 10-second default because the API is
     * hosted on Render's free tier, which sleeps when idle and can take up to
     * a minute to respond to the first request.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    /**
     * JSON parser. KotlinJsonAdapterFactory uses reflection to map JSON keys
     * onto Kotlin data class properties (no code generation required).
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    /** Retrofit instance combining the base URL, JSON converter and HTTP client. */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

    /**
     * Retrofit-generated implementation of [ApiService].
     *
     * Provided through a module rather than @Inject because ApiService is an
     * interface with no constructor — Hilt cannot build one on its own.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}