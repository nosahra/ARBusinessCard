package com.example.dummy_database.tts

/**
 * Provides a singleton Retrofit client for interacting with the Google Cloud Text-to-Speech API.
 * Configures Retrofit with the base URL and JSON converter.
 *
 * Sole Contributor: Newton
 */

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides a lazily initialized instance of the GoogleTTSService interface.
 * The Retrofit client is built only on the first access to this property.
 * Configured to communicate with the Google Cloud TTS API and use Gson for JSON handling.
 */
object RetrofitClient {
    // Base URL for the Google TTS API endpoint
    private const val BASE_URL = "https://texttospeech.googleapis.com/"

    val service: GoogleTTSService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleTTSService::class.java)
    }
}
