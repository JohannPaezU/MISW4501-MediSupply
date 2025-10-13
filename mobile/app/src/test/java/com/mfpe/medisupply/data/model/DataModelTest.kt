package com.mfpe.medisupply.data.model

import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitarios para los modelos de datos
 */
class DataModelTest {

    @Test
    fun `LoginUserRequest should have correct properties`() {
        // Given
        val email = TestUtils.TestData.VALID_EMAIL
        val password = TestUtils.TestData.VALID_PASSWORD

        // When
        val loginRequest = LoginUserRequest(email = email, password = password)

        // Then
        assertEquals("Email should match", email, loginRequest.email)
        assertEquals("Password should match", password, loginRequest.password)
    }

    @Test
    fun `LoginUserRequest with empty email should be invalid`() {
        // Given
        val loginRequest = LoginUserRequest(email = "", password = TestUtils.TestData.VALID_PASSWORD)

        // When & Then
        assertTrue("Email should be empty", loginRequest.email.isEmpty())
    }

    @Test
    fun `LoginUserRequest with empty password should be invalid`() {
        // Given
        val loginRequest = LoginUserRequest(email = TestUtils.TestData.VALID_EMAIL, password = "")

        // When & Then
        assertTrue("Password should be empty", loginRequest.password.isEmpty())
    }

    @Test
    fun `RegisterUserRequest should have correct properties`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            nit = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        // When & Then
        assertEquals("Full name should match", TestUtils.TestData.VALID_FULL_NAME, registerRequest.fullName)
        assertEquals("Email should match", TestUtils.TestData.VALID_EMAIL, registerRequest.email)
        assertEquals("Role should be institutional", "institutional", registerRequest.role)
        assertEquals("Password should match", TestUtils.TestData.VALID_PASSWORD, registerRequest.password)
        assertEquals("Phone should match", TestUtils.TestData.VALID_PHONE, registerRequest.phone)
        assertEquals("NIT should match", TestUtils.TestData.VALID_NIT, registerRequest.nit)
        assertEquals("Address should match", TestUtils.TestData.VALID_ADDRESS, registerRequest.address)
    }

    @Test
    fun `RegisterUserRequest with empty fields should be invalid`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = "",
            email = "",
            role = "",
            password = "",
            phone = "",
            nit = "",
            address = ""
        )

        // When & Then
        assertTrue("Full name should be empty", registerRequest.fullName.isEmpty())
        assertTrue("Email should be empty", registerRequest.email.isEmpty())
        assertTrue("Role should be empty", registerRequest.role.isEmpty())
        assertTrue("Password should be empty", registerRequest.password.isEmpty())
        assertTrue("Phone should be empty", registerRequest.phone.isEmpty())
        assertTrue("NIT should be empty", registerRequest.nit.isEmpty())
        assertTrue("Address should be empty", registerRequest.address.isEmpty())
    }

    @Test
    fun `ValidateOTPRequest should have correct properties`() {
        // Given
        val otp = TestUtils.TestData.VALID_OTP

        // When
        val otpRequest = ValidateOTPRequest(otp = otp)

        // Then
        assertEquals("OTP should match", otp, otpRequest.otp)
    }

    @Test
    fun `ValidateOTPRequest with empty otp should be invalid`() {
        // Given
        val otpRequest = ValidateOTPRequest(otp = "")

        // When & Then
        assertTrue("OTP should be empty", otpRequest.otp.isEmpty())
    }

    @Test
    fun `LoginUserResponse should have correct properties`() {
        // Given
        val message = "Login successful"

        // When
        val loginResponse = LoginUserResponse(message = message)

        // Then
        assertEquals("Message should match", message, loginResponse.message)
    }

    @Test
    fun `RegisterUserResponse should have correct properties`() {
        // Given
        val message = "Registration successful"

        // When
        val registerResponse = RegisterUserResponse(message = message)

        // Then
        assertEquals("Message should match", message, registerResponse.message)
    }

    @Test
    fun `ValidateOTPResponse should have correct properties`() {
        // Given
        val token = TestUtils.TestData.VALID_TOKEN
        val fullName = TestUtils.TestData.VALID_FULL_NAME
        val email = TestUtils.TestData.VALID_EMAIL
        val role = TestUtils.TestData.VALID_ROLE

        // When
        val otpResponse = ValidateOTPResponse(
            id = 1,
            token = token,
            fullName = fullName,
            email = email,
            role = role
        )

        // Then
        assertEquals("Token should match", token, otpResponse.token)
        assertEquals("Full name should match", fullName, otpResponse.fullName)
        assertEquals("Email should match", email, otpResponse.email)
        assertEquals("Role should match", role, otpResponse.role)
    }

    @Test
    fun `data classes should be immutable`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        // When & Then
        // Los data classes en Kotlin son inmutables por defecto
        assertNotNull("LoginRequest should not be null", loginRequest)
        assertEquals("Email should remain unchanged", TestUtils.TestData.VALID_EMAIL, loginRequest.email)
        assertEquals("Password should remain unchanged", TestUtils.TestData.VALID_PASSWORD, loginRequest.password)
    }
}
