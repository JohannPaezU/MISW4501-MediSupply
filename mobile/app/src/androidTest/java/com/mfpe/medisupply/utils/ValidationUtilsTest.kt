package com.mfpe.medisupply.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValidationUtilsTest {
    @Test
    fun isValidEmail_shouldReturnTrue_forValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user.name@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user+tag@example.co.uk"))
    }

    @Test
    fun isValidEmail_shouldReturnFalse_forEmptyEmail() {
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun isValidEmail_shouldReturnFalse_forInvalidEmail() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail("test@.com"))
        assertFalse(ValidationUtils.isValidEmail("@example.com"))
        assertFalse(ValidationUtils.isValidEmail("test@example"))
        assertFalse(ValidationUtils.isValidEmail("test@"))
        assertFalse(ValidationUtils.isValidEmail("test"))
    }
}

