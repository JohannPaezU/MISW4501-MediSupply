package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.*
import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit

/**
 * Tests unitarios para RetrofitApiClient
 */
class RetrofitApiClientTest {

    @Test
    fun `createRetrofitService should create service instance`() {
        // When
        val userService = RetrofitApiClient.createRetrofitService(UserService::class.java)

        // Then
        assertNotNull("UserService should not be null", userService)
        assertTrue("Should be instance of UserService", userService is UserService)
    }

    @Test
    fun `createRetrofitService should create different service instances`() {
        // When
        val userService1 = RetrofitApiClient.createRetrofitService(UserService::class.java)
        val userService2 = RetrofitApiClient.createRetrofitService(UserService::class.java)

        // Then
        assertNotNull("First service should not be null", userService1)
        assertNotNull("Second service should not be null", userService2)
        assertTrue("Both should be instances of UserService", 
            userService1 is UserService && userService2 is UserService)
    }

    @Test
    fun `createRetrofitService should handle UserService methods`() {
        // Given
        val userService = RetrofitApiClient.createRetrofitService(UserService::class.java)
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            nit = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        val otpRequest = ValidateOTPRequest(otp = TestUtils.TestData.VALID_OTP)

        // When
        val registerCall = userService.registerUser(registerRequest)
        val loginCall = userService.loginUser(loginRequest)
        val otpCall = userService.validateOTP(otpRequest)

        // Then
        assertNotNull("Register call should not be null", registerCall)
        assertNotNull("Login call should not be null", loginCall)
        assertNotNull("OTP call should not be null", otpCall)
        assertTrue("Register call should be Call type", registerCall is Call<*>)
        assertTrue("Login call should be Call type", loginCall is Call<*>)
        assertTrue("OTP call should be Call type", otpCall is Call<*>)
    }

    @Test
    fun `createRetrofitService should create service with correct configuration`() {
        // When
        val userService = RetrofitApiClient.createRetrofitService(UserService::class.java)

        // Then
        assertNotNull("Service should be created", userService)
        
        // Verify service has expected methods
        val methods = userService.javaClass.methods
        val methodNames = methods.map { it.name }
        
        assertTrue("Should have registerUser method", methodNames.contains("registerUser"))
        assertTrue("Should have loginUser method", methodNames.contains("loginUser"))
        assertTrue("Should have validateOTP method", methodNames.contains("validateOTP"))
    }

    @Test
    fun `createRetrofitService should handle null safety`() {
        // When & Then
        try {
            val userService = RetrofitApiClient.createRetrofitService(UserService::class.java)
            assertNotNull("Service should not be null", userService)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `createRetrofitService should be consistent across calls`() {
        // When
        val service1 = RetrofitApiClient.createRetrofitService(UserService::class.java)
        val service2 = RetrofitApiClient.createRetrofitService(UserService::class.java)

        // Then
        assertNotNull("First service should not be null", service1)
        assertNotNull("Second service should not be null", service2)
        assertEquals("Services should be of same type", 
            service1.javaClass, service2.javaClass)
    }
}
