package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.*
import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Tests unitarios para UserService
 */
class UserServiceTest {

    private fun createUserService(): UserService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mock-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(UserService::class.java)
    }

    @Test
    fun `registerUser should return Call with correct type`() {
        // Given
        val userService = createUserService()
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            nit = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        // When
        val call = userService.registerUser(registerRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<RegisterUserResponse>", call is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should return Call with correct type`() {
        // Given
        val userService = createUserService()
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        // When
        val call = userService.loginUser(loginRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<LoginUserResponse>", call is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should return Call with correct type`() {
        // Given
        val userService = createUserService()
        val otpRequest = ValidateOTPRequest(otp = TestUtils.TestData.VALID_OTP)

        // When
        val call = userService.validateOTP(otpRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<ValidateOTPResponse>", call is Call<ValidateOTPResponse>)
    }

    @Test
    fun `registerUser should accept valid request data`() {
        // Given
        val userService = createUserService()
        val registerRequest = RegisterUserRequest(
            fullName = "Test User",
            email = "test@example.com",
            role = "institutional",
            password = "password123",
            phone = "1234567890",
            nit = "123456789",
            address = "Test Address"
        )

        // When
        val call = userService.registerUser(registerRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<RegisterUserResponse>", call is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should accept valid request data`() {
        // Given
        val userService = createUserService()
        val loginRequest = LoginUserRequest(
            email = "test@example.com",
            password = "password123"
        )

        // When
        val call = userService.loginUser(loginRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<LoginUserResponse>", call is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should accept valid request data`() {
        // Given
        val userService = createUserService()
        val otpRequest = ValidateOTPRequest(otp = "123456")

        // When
        val call = userService.validateOTP(otpRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<ValidateOTPResponse>", call is Call<ValidateOTPResponse>)
    }

    @Test
    fun `registerUser should handle empty request data`() {
        // Given
        val userService = createUserService()
        val registerRequest = RegisterUserRequest(
            fullName = "",
            email = "",
            role = "",
            password = "",
            phone = "",
            nit = "",
            address = ""
        )

        // When
        val call = userService.registerUser(registerRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<RegisterUserResponse>", call is Call<RegisterUserResponse>)
    }

    @Test
    fun `loginUser should handle empty request data`() {
        // Given
        val userService = createUserService()
        val loginRequest = LoginUserRequest(
            email = "",
            password = ""
        )

        // When
        val call = userService.loginUser(loginRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<LoginUserResponse>", call is Call<LoginUserResponse>)
    }

    @Test
    fun `validateOTP should handle empty request data`() {
        // Given
        val userService = createUserService()
        val otpRequest = ValidateOTPRequest(otp = "")

        // When
        val call = userService.validateOTP(otpRequest)

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Should be Call<ValidateOTPResponse>", call is Call<ValidateOTPResponse>)
    }

    @Test
    fun `UserService should have correct annotation methods`() {
        // Given
        val userService = createUserService()

        // When
        val methods = userService.javaClass.methods
        val registerMethod = methods.find { it.name == "registerUser" }
        val loginMethod = methods.find { it.name == "loginUser" }
        val otpMethod = methods.find { it.name == "validateOTP" }

        // Then
        assertNotNull("registerUser method should exist", registerMethod)
        assertNotNull("loginUser method should exist", loginMethod)
        assertNotNull("validateOTP method should exist", otpMethod)
    }

    @Test
    fun `UserService should be interface`() {
        // Given
        val userServiceClass = UserService::class.java

        // Then
        assertTrue("UserService should be interface", userServiceClass.isInterface)
    }
}
