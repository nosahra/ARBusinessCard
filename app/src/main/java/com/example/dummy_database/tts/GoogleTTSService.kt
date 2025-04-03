package com.example.dummy_database.tts



import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleTTSService {
    @POST("v1/text:synthesize")
    suspend fun synthesize(
        @Body request: TTSRequest,
        @Query("key") apiKey: String
    ): TTSResponse
}
