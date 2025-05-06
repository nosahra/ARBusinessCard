package com.example.dummy_database.tts

// Data classes for the Google TTS API request and response
// Sole Contributor: Newton

// top-level request body to synthesize speech
data class TTSRequest(
    val input: Input,
    val voice: Voice,
    val audioConfig: AudioConfig
)

// Text that will be spoken
data class Input(
    val text: String
)

// Voice selection parameters
data class Voice(
    val languageCode: String = "en-GB",   // Set the language code to GB English
    val name: String? = null, // Allow dynamic setting of the voice
    val ssmlGender: String = "NEUTRAL",    //  by default gender set to neutral
)

// Audio output configuration
data class AudioConfig(
    val audioEncoding: String="MP3"
)

// Response body contains the generated audio
data class TTSResponse(
    val audioContent: String
)
