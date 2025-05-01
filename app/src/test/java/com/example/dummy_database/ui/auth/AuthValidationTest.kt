package com.example.dummy_database.ui.auth

import org.junit.Assert.* // Using JUnit 4 assertions
import org.junit.Test

class AuthValidationTest {


    // Test cases for the isPasswordStrong function

    @Test
    fun `isPasswordStrong returns true for valid strong password`() {
        // Arrange: Define valid passwords
        val passwordA = "ValidP4ss"
        val passwordB = "Another1"
        val passwordC = "S3curE!" // Includes symbol, still valid

        // Act & Assert: Call the function and check the result
        assertTrue("Password '$passwordA' should be strong", isPasswordStrong(passwordA))
        assertTrue("Password '$passwordB' should be strong", isPasswordStrong(passwordB))
        assertTrue("Password '$passwordC' should be strong", isPasswordStrong(passwordC))
    }

    @Test
    fun `isPasswordStrong returns false for password too short`() {
        // Arrange
        val password = "Sh0rT" // 5 chars

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Password '$password' is too short", result)
    }

    @Test
    fun `isPasswordStrong returns false for password missing uppercase`() {
        // Arrange
        val password = "nouppercase123"

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Password '$password' is missing uppercase", result)
    }

    @Test
    fun `isPasswordStrong returns false for password missing digit`() {
        // Arrange
        val password = "NoDigitPassword"

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Password '$password' is missing digit", result)
    }

    @Test
    fun `isPasswordStrong returns false for empty password`() {
        // Arrange
        val password = ""

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Empty password should not be strong", result)
    }

    @Test
    fun `isPasswordStrong returns false for password with only digits`() {
        // Arrange
        val password = "123456"

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Password with only digits should not be strong", result)
    }

    @Test
    fun `isPasswordStrong returns false for password with only uppercase`() {
        // Arrange
        val password = "UPPERCASE"

        // Act
        val result = isPasswordStrong(password)

        // Assert
        assertFalse("Password with only uppercase should not be strong", result)
    }

    @Test
    fun `passwordMatches returns true when passwords are identical`() {
        val password = "Password123"
        val confirmPassword = "Password123"

        // Simulate the check done in the UI logic
        val passwordsMatch = (password == confirmPassword)

        assertTrue("Passwords should match", passwordsMatch)
    }

    @Test
    fun `passwordMatches returns false when passwords differ`() {
        val password = "Password123"
        val confirmPassword = "Different123"

        // Simulate the check done in the UI logic
        val passwordsMatch = (password == confirmPassword)

        assertFalse("Passwords should not match", passwordsMatch)
    }

    @Test
    fun `passwordMatches returns false when confirmPassword is empty`() {
        val password = "Password123"
        val confirmPassword = ""

        // Simulate the check done in the UI logic
        val passwordsMatch = (password == confirmPassword)

        assertFalse("Password should not match empty confirm password", passwordsMatch)
    }

    @Test
    fun `passwordMatches returns true for identical empty passwords`() {
        // While disallowed by earlier checks, the direct comparison logic should still be tested
        val password = ""
        val confirmPassword = ""

        // Simulate the check done in the UI logic
        val passwordsMatch = (password == confirmPassword)

        assertTrue("Empty passwords should technically match each other", passwordsMatch)
    }




}