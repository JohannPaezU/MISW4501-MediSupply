package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.*
import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call

/**
 * Tests unitarios para UserRepository
 */
class UserRepositoryTest {

    private val userRepository = UserRepository()

    @Test
    fun `registerUser should return Call with correct type`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            doi = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        // When
        val result = userRepository.registerUser(registerRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<RegisterUserResponse>", result is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should return Call with correct type`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        // When
        val result = userRepository.loginUser(loginRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<LoginUserResponse>", result is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should return Call with correct type`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)

        // When
        val result = userRepository.validateOTP(otpRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<ValidateOTPResponse>", result is Call<ValidateOTPResponse>)
    }

    @Test
    fun `registerUser should handle different request data`() {
        // Given
        val registerRequest1 = RegisterUserRequest(
            fullName = "User One",
            email = "user1@example.com",
            role = "institutional",
            password = "password1",
            phone = "1111111111",
            doi = "111111111",
            address = "Address One"
        )

        val registerRequest2 = RegisterUserRequest(
            fullName = "User Two",
            email = "user2@example.com",
            role = "individual",
            password = "password2",
            phone = "2222222222",
            doi = "222222222",
            address = "Address Two"
        )

        // When
        val result1 = userRepository.registerUser(registerRequest1)
        val result2 = userRepository.registerUser(registerRequest2)

        // Then
        assertNotNull("First result should not be null", result1)
        assertNotNull("Second result should not be null", result2)
        assertTrue("First result should be Call<RegisterUserResponse>", result1 is Call<RegisterUserResponse>)
        assertTrue("Second result should be Call<RegisterUserResponse>", result2 is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should handle different request data`() {
        // Given
        val loginRequest1 = LoginUserRequest(
            email = "user1@example.com",
            password = "password1"
        )

        val loginRequest2 = LoginUserRequest(
            email = "user2@example.com",
            password = "password2"
        )

        // When
        val result1 = userRepository.loginUser(loginRequest1)
        val result2 = userRepository.loginUser(loginRequest2)

        // Then
        assertNotNull("First result should not be null", result1)
        assertNotNull("Second result should not be null", result2)
        assertTrue("First result should be Call<LoginUserResponse>", result1 is Call<LoginUserResponse>)
        assertTrue("Second result should be Call<LoginUserResponse>", result2 is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should handle different request data`() {
        // Given
        val otpRequest1 = ValidateOTPRequest(otpCode = "123456", email = TestUtils.TestData.VALID_EMAIL)
        val otpRequest2 = ValidateOTPRequest(otpCode = "654321", email = TestUtils.TestData.VALID_EMAIL)

        // When
        val result1 = userRepository.validateOTP(otpRequest1)
        val result2 = userRepository.validateOTP(otpRequest2)

        // Then
        assertNotNull("First result should not be null", result1)
        assertNotNull("Second result should not be null", result2)
        assertTrue("First result should be Call<ValidateOTPResponse>", result1 is Call<ValidateOTPResponse>)
        assertTrue("Second result should be Call<ValidateOTPResponse>", result2 is Call<ValidateOTPResponse>)
    }

    @Test
    fun `registerUser should handle empty request data`() {
        // Given
        val emptyRequest = RegisterUserRequest(
            fullName = "",
            email = "",
            role = "",
            password = "",
            phone = "",
            doi = "",
            address = ""
        )

        // When
        val result = userRepository.registerUser(emptyRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<RegisterUserResponse>", result is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should handle empty request data`() {
        // Given
        val emptyRequest = LoginUserRequest(
            email = "",
            password = ""
        )

        // When
        val result = userRepository.loginUser(emptyRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<LoginUserResponse>", result is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should handle empty request data`() {
        // Given
        val emptyRequest = ValidateOTPRequest(otpCode = "", email = TestUtils.TestData.VALID_EMAIL)

        // When
        val result = userRepository.validateOTP(emptyRequest)

        // Then
        assertNotNull("Result should not be null", result)
        assertTrue("Should be Call<ValidateOTPResponse>", result is Call<ValidateOTPResponse>)
    }

    @Test
    fun `UserRepository should return correct Call types`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            doi = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)

        // When
        val registerResult = userRepository.registerUser(registerRequest)
        val loginResult = userRepository.loginUser(loginRequest)
        val otpResult = userRepository.validateOTP(otpRequest)

        // Then
        assertTrue("Register result should be Call<RegisterUserResponse>", 
            registerResult is Call<RegisterUserResponse>)
        assertTrue("Login result should be Call<LoginUserResponse>", 
            loginResult is Call<LoginUserResponse>)
        assertTrue("OTP result should be Call<ValidateOTPResponse>", 
            otpResult is Call<ValidateOTPResponse>)
    }
}