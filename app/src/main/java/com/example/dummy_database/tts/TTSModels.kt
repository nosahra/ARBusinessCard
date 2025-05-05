package com.example.dummy_database.tts

data class TTSRequest(
    val input: Input,
    val voice: Voice,
    val audioConfig: AudioConfig
)

data class Input(
    val text: String
)

data class Voice(
    val languageCode: String = "en-GB",   // Set the language code to GB English
    val name: String? = null, // Allow dynamic setting of the voice
    val ssmlGender: String = "NEUTRAL",    //  by default gender set to neutral
)

data class AudioConfig(
    val audioEncoding: String="MP3"
)

data class TTSResponse(
    val audioContent: String
)
