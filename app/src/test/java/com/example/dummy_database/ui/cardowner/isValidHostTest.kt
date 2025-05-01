package com.example.dummy_database.ui.cardowner

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class isValidHostTest {

    @Test fun `valid plain host`() {
        assertTrue(isValidHost("example.com", "example.com"))
        assertTrue(isValidHost("sub.example.com", "example.com"))
    }

    @Test fun `valid with http scheme`() {
        assertTrue(isValidHost("http://example.com", "example.com"))
        assertTrue(isValidHost("https://foo.example.com", "example.com"))
    }

    @Test fun `invalid hosts`() {
        assertFalse(isValidHost("notexample.com", "example.com"))
        assertFalse(isValidHost("example.org", "example.com"))
        assertFalse(isValidHost("http://evil.com?host=example.com", "example.com"))
    }

    @Test fun `empty or malformed url`() {
        assertFalse(isValidHost("", "example.com"))
        assertFalse(isValidHost("::::", "example.com"))
    }
}

