package com.example.dummy_database.tts

import org.junit.Assert.*
import org.junit.Test

class TTSUtilTest {
    @Test fun mapVoiceParams_male() {
        val (name, gender) = TTSUtil.mapVoiceParams("male")
        assertEquals("en-GB-Journey-D", name)
        assertEquals("MALE", gender)
    }

    @Test fun mapVoiceParams_female_mixedCase() {
        val (name, gender) = TTSUtil.mapVoiceParams("FeMaLe")
        assertEquals("en-GB-Journey-F", name)
        assertEquals("FEMALE", gender)
    }

    @Test fun mapVoiceParams_other_defaultsNeutral() {
        val (name, gender) = TTSUtil.mapVoiceParams("xyz")
        assertEquals("en-GB-Journey-O", name)
        assertEquals("NEUTRAL", gender)
    }
}
