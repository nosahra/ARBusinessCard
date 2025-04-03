package com.example.dummy_database.tts

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import com.example.dummy_database.tts.AudioConfig
import com.example.dummy_database.tts.Input
import com.example.dummy_database.tts.TTSRequest
import com.example.dummy_database.tts.Voice
import com.example.dummy_database.tts.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun synthesizeAndPlay(context: Context, text: String, voicePreference: String) {
    // Map the user's voice preference to the corresponding Journey voice parameters.
    val (voiceName, ssmlGender) = when (voicePreference.uppercase()) {
        "MALE" -> "en-GB-Journey-D" to "MALE"
        "FEMALE" -> "en-GB-Journey-F" to "FEMALE"
        else -> "en-GB-Journey-O" to "NEUTRAL"  // fallback option
    }

    // Build the TTS request payload.
    val request = TTSRequest(
        input = Input(text),
        voice = Voice(
            languageCode = "en-GB",
            name = voiceName,
            ssmlGender = ssmlGender
        ),
        audioConfig = AudioConfig(audioEncoding = "MP3")
    )

    // IMPORTANT: Replace this API key with your actual key (store it securely!)
    val apiKey = "AIzaSyDoba8eStqzTUAr8O3COyc-eGRFzmWHank"

    try {
        // Call the TTS API (this is a suspend function).
        val response = RetrofitClient.service.synthesize(request, apiKey)

        // Decode the Base64 audio content.
        val audioBytes = Base64.decode(response.audioContent, Base64.DEFAULT)

        // Write the decoded bytes to a temporary file.
        val tempFile = File.createTempFile("tts", "mp3", context.cacheDir)
        tempFile.writeBytes(audioBytes)

        // Use MediaPlayer to play the audio.
        withContext(Dispatchers.Main) {
            MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        }

        Log.d("TTSUtil", "TTS synthesis and playback succeeded.")
    } catch (e: Exception) {
        Log.e("TTSUtil", "Error during TTS synthesis: ${e.message}")
    }
}
