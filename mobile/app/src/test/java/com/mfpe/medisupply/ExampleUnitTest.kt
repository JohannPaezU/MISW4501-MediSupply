package com.mfpe.medisupply

import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitarios mejorados para la aplicación MediSupply
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testUtils_shouldProvideTestData() {
        // Verificar que TestUtils proporcione datos de prueba válidos
        assertNotNull("Valid email should not be null", TestUtils.TestData.VALID_EMAIL)
        assertNotNull("Valid password should not be null", TestUtils.TestData.VALID_PASSWORD)
        assertNotNull("Valid OTP should not be null", TestUtils.TestData.VALID_OTP)
        assertNotNull("Valid full name should not be null", TestUtils.TestData.VALID_FULL_NAME)
        assertNotNull("Valid NIT should not be null", TestUtils.TestData.VALID_NIT)
        assertNotNull("Valid address should not be null", TestUtils.TestData.VALID_ADDRESS)
        assertNotNull("Valid phone should not be null", TestUtils.TestData.VALID_PHONE)
        assertNotNull("Valid token should not be null", TestUtils.TestData.VALID_TOKEN)
        assertNotNull("Valid role should not be null", TestUtils.TestData.VALID_ROLE)
    }

    @Test
    fun testUtils_shouldHaveValidEmailFormat() {
        // Verificar que el email de prueba tenga formato válido
        val email = TestUtils.TestData.VALID_EMAIL
        assertTrue("Email should contain @", email.contains("@"))
        assertTrue("Email should contain domain", email.contains("."))
    }

    @Test
    fun testUtils_shouldHaveValidPasswordLength() {
        // Verificar que la contraseña de prueba tenga longitud adecuada
        val password = TestUtils.TestData.VALID_PASSWORD
        assertTrue("Password should be at least 8 characters", password.length >= 8)
    }

    @Test
    fun testUtils_shouldHaveValidOTPLength() {
        // Verificar que el OTP de prueba tenga longitud adecuada
        val otp = TestUtils.TestData.VALID_OTP
        assertTrue("OTP should be 6 digits", otp.length == 6)
        assertTrue("OTP should contain only digits", otp.all { it.isDigit() })
    }

    @Test
    fun testUtils_shouldHaveValidPhoneFormat() {
        // Verificar que el teléfono de prueba tenga formato válido
        val phone = TestUtils.TestData.VALID_PHONE
        assertTrue("Phone should start with 3", phone.startsWith("3"))
        assertTrue("Phone should be 10 digits", phone.length == 10)
        assertTrue("Phone should contain only digits", phone.all { it.isDigit() })
    }

    @Test
    fun testUtils_shouldHaveValidNITFormat() {
        // Verificar que el NIT de prueba tenga formato válido
        val nit = TestUtils.TestData.VALID_NIT
        assertTrue("NIT should be 9 digits", nit.length == 9)
        assertTrue("NIT should contain only digits", nit.all { it.isDigit() })
    }

    @Test
    fun testUtils_shouldHaveValidRole() {
        // Verificar que el rol de prueba sea válido
        val role = TestUtils.TestData.VALID_ROLE
        assertEquals("Role should be institutional", "institutional", role)
    }

    @Test
    fun testUtils_shouldHaveValidToken() {
        // Verificar que el token de prueba tenga formato válido
        val token = TestUtils.TestData.VALID_TOKEN
        assertTrue("Token should not be empty", token.isNotEmpty())
        assertTrue("Token should contain dots", token.contains("."))
    }

    @Test
    fun testUtils_shouldHaveValidFullName() {
        // Verificar que el nombre completo de prueba sea válido
        val fullName = TestUtils.TestData.VALID_FULL_NAME
        assertTrue("Full name should contain space", fullName.contains(" "))
        assertTrue("Full name should have at least 2 words", fullName.split(" ").size >= 2)
    }

    @Test
    fun testUtils_shouldHaveValidAddress() {
        // Verificar que la dirección de prueba sea válida
        val address = TestUtils.TestData.VALID_ADDRESS
        assertTrue("Address should not be empty", address.isNotEmpty())
        assertTrue("Address should contain numbers", address.any { it.isDigit() })
    }
}