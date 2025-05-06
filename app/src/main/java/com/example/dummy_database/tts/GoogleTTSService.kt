package com.example.dummy_database.tts

// Retrofit interface for the synthesize call
// Sole Contributor: Newton


import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleTTSService {
    //// Sends a TTSRequest to the Cloud TTS API and returns the generated audio
    @POST("v1/text:synthesize")
    suspend fun synthesize(         // suspend function to make it asynchronous
        @Body request: TTSRequest,
        @Query("key") apiKey: String
    ): TTSResponse
}
