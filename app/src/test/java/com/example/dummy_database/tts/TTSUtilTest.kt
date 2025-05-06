package com.example.dummy_database.tts

//Unit Test(Newton) for TTSUtil.kt (checks if an input voice preference is mapped correctly with actual voice preference)

import org.junit.Assert.*
import org.junit.Test



class TTSUtilTest {


    @Test
    fun `mapVoiceParams returns correct values for MALE preference`() {
        // Arrange
        val preference = "MALE"
        val expectedName = "en-GB-Journey-D"
        val expectedGender = "MALE"

        // Act
        val (actualName, actualGender) = TTSUtil.mapVoiceParams(preference)

        // Assert
        assertEquals(expectedName, actualName)
        assertEquals(expectedGender, actualGender)
    }

    @Test
    fun `mapVoiceParams returns correct values for FEMALE preference`() {
        // Arrange
        val preference = "FEMALE"
        val expectedName = "en-GB-Journey-F"
        val expectedGender = "FEMALE"

        // Act
        val (actualName, actualGender) = TTSUtil.mapVoiceParams(preference)

        // Assert
        assertEquals(expectedName, actualName)
        assertEquals(expectedGender, actualGender)
    }

    @Test
    fun `mapVoiceParams returns fallback values for unknown preference`() {
        // Arrange
        val preference = "UNKNOWN"
        val expectedName = "en-GB-Journey-O"
        val expectedGender = "NEUTRAL"

        // Act
        val (actualName, actualGender) = TTSUtil.mapVoiceParams(preference)

        // Assert
        assertEquals(expectedName, actualName)
        assertEquals(expectedGender, actualGender)
    }

    @Test
    fun `mapVoiceParams returns fallback values for empty preference`() {
        // Arrange
        val preference = ""
        val expectedName = "en-GB-Journey-O"
        val expectedGender = "NEUTRAL"

        // Act
        val (actualName, actualGender) = TTSUtil.mapVoiceParams(preference)

        // Assert
        assertEquals(expectedName, actualName)
        assertEquals(expectedGender, actualGender)
    }

    @Test
    fun `mapVoiceParams handles different casing`() {
        // Arrange
        val preferenceMaleLower = "male"
        val preferenceFemaleMixed = "FeMaLe"
        val expectedMaleName = "en-GB-Journey-D"
        val expectedFemaleName = "en-GB-Journey-F"

        // Act
        val (actualMaleName, _) = TTSUtil.mapVoiceParams(preferenceMaleLower)
        val (actualFemaleName, _) = TTSUtil.mapVoiceParams(preferenceFemaleMixed)

        // Assert
        assertEquals(expectedMaleName, actualMaleName)
        assertEquals(expectedFemaleName, actualFemaleName)
    }

}
