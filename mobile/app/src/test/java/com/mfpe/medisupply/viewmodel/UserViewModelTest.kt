package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.LoginUserRequest
import com.mfpe.medisupply.data.model.LoginUserResponse
import com.mfpe.medisupply.data.model.RegisterUserRequest
import com.mfpe.medisupply.data.model.RegisterUserResponse
import com.mfpe.medisupply.data.model.ValidateOTPRequest
import com.mfpe.medisupply.data.model.ValidateOTPResponse
import com.mfpe.medisupply.data.model.OTPUser
import com.mfpe.medisupply.data.repository.UserRepository
import com.mfpe.medisupply.utils.TestUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    @Mock
    private lateinit var mockRegisterCall: Call<RegisterUserResponse>

    @Mock
    private lateinit var mockLoginCall: Call<LoginUserResponse>

    @Mock
    private lateinit var mockValidateOTPCall: Call<ValidateOTPResponse>

    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        userViewModel = UserViewModel(mockUserRepository)
    }

    // ========== BASIC TESTS ==========

    @Test
    fun `UserViewModel should be created successfully`() {
        // Given & When
        val viewModel = UserViewModel()
        
        // Then
        assertNotNull(viewModel)
    }

    @Test
    fun `UserViewModel should have correct class name`() {
        // Given
        val viewModel = UserViewModel()
        
        // When & Then
        assertEquals("UserViewModel", viewModel.javaClass.simpleName)
    }

    @Test
    fun `UserViewModel should extend ViewModel`() {
        // Given
        val viewModel = UserViewModel()
        
        // When & Then
        assertTrue("UserViewModel should extend ViewModel", viewModel is ViewModel)
    }

    @Test
    fun `UserViewModel should be properly configured`() {
        // Given
        val viewModelClass = UserViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `UserViewModel should have registerUser method`() {
        // Given
        val viewModelClass = UserViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("registerUser", 
            RegisterUserRequest::class.java, 
            kotlin.Function2::class.java))
    }

    @Test
    fun `UserViewModel should have loginUser method`() {
        // Given
        val viewModelClass = UserViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("loginUser", 
            LoginUserRequest::class.java, 
            kotlin.Function2::class.java))
    }

    @Test
    fun `UserViewModel should have validateOTP method`() {
        // Given
        val viewModelClass = UserViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("validateOTP", 
            ValidateOTPRequest::class.java, 
            kotlin.Function3::class.java))
    }

    // ========== REGISTER USER TESTS ==========

    @Test
    fun `registerUser should call repository`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "seller",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = "3001234567",
            doi = "12345678",
            address = "Calle 123 #45-67"
        )
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        
        // When
        userViewModel.registerUser(registerRequest) { _, _ -> }
        
        // Then
        verify(mockUserRepository).registerUser(registerRequest)
    }

    @Test
    fun `registerUser should handle successful response`() {
        // Given
        val registerRequest = createValidRegisterRequest()
        val mockResponse = mock(Response::class.java) as Response<RegisterUserResponse>
        val registerResponse = RegisterUserResponse(
            id = "user123",
            createdAt = java.util.Date()
        )

        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(registerResponse)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<RegisterUserResponse>
            callback.onResponse(mockRegisterCall, mockResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        var resultSuccess = false
        var resultMessage = ""

        // When
        userViewModel.registerUser(registerRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockRegisterCall).enqueue(any())
        assertTrue(resultSuccess)
        assertEquals("user123", resultMessage)
    }

    @Test
    fun `registerUser should handle failed response`() {
        // Given
        val registerRequest = createValidRegisterRequest()
        val mockResponse = mock(Response::class.java) as Response<RegisterUserResponse>

        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(400)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<RegisterUserResponse>
            callback.onResponse(mockRegisterCall, mockResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""

        // When
        userViewModel.registerUser(registerRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockRegisterCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Error registering user"))
    }

    @Test
    fun `registerUser should handle network failure`() {
        // Given
        val registerRequest = createValidRegisterRequest()
        val exception = Exception("Network error")

        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<RegisterUserResponse>
            callback.onFailure(mockRegisterCall, exception)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""

        // When
        userViewModel.registerUser(registerRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockRegisterCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Connection error"))
    }

    @Test
    fun `registerUser should handle different user roles`() {
        // Given
        val roles = listOf("seller", "institutional", "commercial")

        roles.forEach { role ->
            val registerRequest = RegisterUserRequest(
                fullName = "Test User",
                email = "test@example.com",
                role = role,
                password = "Password123",
                phone = "3001234567",
                doi = "12345678",
                address = "Test Address"
            )

            `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)

            // When
            userViewModel.registerUser(registerRequest) { _, _ -> }

            // Then
            verify(mockUserRepository).registerUser(registerRequest)
        }
    }

    // ========== LOGIN USER TESTS ==========

    @Test
    fun `loginUser should call repository`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
        
        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)

        // When
        userViewModel.loginUser(loginRequest) { _, _ -> }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
    }

    @Test
    fun `loginUser should handle successful response`() {
        // Given
        val loginRequest = createValidLoginRequest()
        val mockResponse = mock(Response::class.java) as Response<LoginUserResponse>
        val loginResponse = LoginUserResponse(message = "Login successful")

        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(loginResponse)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<LoginUserResponse>
            callback.onResponse(mockLoginCall, mockResponse)
            null
        }.`when`(mockLoginCall).enqueue(any())

        var resultSuccess = false
        var resultMessage = ""

        // When
        userViewModel.loginUser(loginRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockLoginCall).enqueue(any())
        assertTrue(resultSuccess)
        assertEquals("Login successful", resultMessage)
    }

    @Test
    fun `loginUser should handle failed response`() {
        // Given
        val loginRequest = createValidLoginRequest()
        val mockResponse = mock(Response::class.java) as Response<LoginUserResponse>

        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(401)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<LoginUserResponse>
            callback.onResponse(mockLoginCall, mockResponse)
            null
        }.`when`(mockLoginCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""

        // When
        userViewModel.loginUser(loginRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockLoginCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Error logging in"))
    }

    @Test
    fun `loginUser should handle network failure`() {
        // Given
        val loginRequest = createValidLoginRequest()
        val exception = Exception("Network error")

        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<LoginUserResponse>
            callback.onFailure(mockLoginCall, exception)
            null
        }.`when`(mockLoginCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""

        // When
        userViewModel.loginUser(loginRequest) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        // Then
        verify(mockLoginCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Connection error"))
    }

    @Test
    fun `loginUser should handle empty credentials`() {
        // Given
        val loginRequest = LoginUserRequest(email = "", password = "")

        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)

        // When
        userViewModel.loginUser(loginRequest) { _, _ -> }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
    }

    @Test
    fun `loginUser should handle special characters in email`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = "test+special@example.com",
            password = "Password123"
        )

        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)

        // When
        userViewModel.loginUser(loginRequest) { _, _ -> }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
    }

    // ========== VALIDATE OTP TESTS ==========

    @Test
    fun `validateOTP should call repository`() {
        // Given
        val validateRequest = ValidateOTPRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            otpCode = "123456"
        )
        
        `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)

        // When
        userViewModel.validateOTP(validateRequest) { _, _, _ -> }

        // Then
        verify(mockUserRepository).validateOTP(validateRequest)
    }

    @Test
    fun `validateOTP should handle successful response`() {
        // Given
        val validateRequest = createValidOTPRequest()
        val mockResponse = mock(Response::class.java) as Response<ValidateOTPResponse>
        val otpUser = OTPUser(
            id = "user123",
            fullName = "Test User",
            email = "test@example.com",
            role = "seller"
        )
        val validateResponse = ValidateOTPResponse(
            accessToken = "token123",
            user = otpUser
        )

        `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(validateResponse)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<ValidateOTPResponse>
            callback.onResponse(mockValidateOTPCall, mockResponse)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        var resultSuccess = false
        var resultMessage = ""
        var resultResponse: ValidateOTPResponse? = null

        // When
        userViewModel.validateOTP(validateRequest) { success, message, response ->
            resultSuccess = success
            resultMessage = message
            resultResponse = response
        }

        // Then
        verify(mockValidateOTPCall).enqueue(any())
        assertTrue(resultSuccess)
        assertEquals("OTP validated.", resultMessage)
        assertNotNull(resultResponse)
        assertEquals("token123", resultResponse?.accessToken)
    }

    @Test
    fun `validateOTP should handle failed response`() {
        // Given
        val validateRequest = createValidOTPRequest()
        val mockResponse = mock(Response::class.java) as Response<ValidateOTPResponse>

        `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(400)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<ValidateOTPResponse>
            callback.onResponse(mockValidateOTPCall, mockResponse)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""
        var resultResponse: ValidateOTPResponse? = null

        // When
        userViewModel.validateOTP(validateRequest) { success, message, response ->
            resultSuccess = success
            resultMessage = message
            resultResponse = response
        }

        // Then
        verify(mockValidateOTPCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Error validating OTP"))
        assertNull(resultResponse)
    }

    @Test
    fun `validateOTP should handle network failure`() {
        // Given
        val validateRequest = createValidOTPRequest()
        val exception = Exception("Network error")

        `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)

        doAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val callback = invocation.arguments[0] as Callback<ValidateOTPResponse>
            callback.onFailure(mockValidateOTPCall, exception)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        var resultSuccess = true
        var resultMessage = ""
        var resultResponse: ValidateOTPResponse? = null

        // When
        userViewModel.validateOTP(validateRequest) { success, message, response ->
            resultSuccess = success
            resultMessage = message
            resultResponse = response
        }

        // Then
        verify(mockValidateOTPCall).enqueue(any())
        assertFalse(resultSuccess)
        assertTrue(resultMessage.contains("Connection error"))
        assertNull(resultResponse)
    }

    @Test
    fun `validateOTP should handle empty request`() {
        // Given
        val emptyRequest = ValidateOTPRequest(email = "", otpCode = "")

        `when`(mockUserRepository.validateOTP(emptyRequest)).thenReturn(mockValidateOTPCall)
        
        // When
        userViewModel.validateOTP(emptyRequest) { _, _, _ -> }

        // Then
        verify(mockUserRepository).validateOTP(emptyRequest)
    }

    @Test
    fun `validateOTP should handle invalid OTP code format`() {
        // Given
        val invalidCodes = listOf("12345", "1234567", "abcdef", "!@#$%^")

        invalidCodes.forEach { code ->
            val validateRequest = ValidateOTPRequest(
                email = "test@example.com",
                otpCode = code
            )

            `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)

            // When
            userViewModel.validateOTP(validateRequest) { _, _, _ -> }

            // Then
            verify(mockUserRepository).validateOTP(validateRequest)
        }
    }

    @Test
    fun `validateOTP should handle different email formats`() {
        // Given
        val emails = listOf(
            "test@example.com",
            "test.user@example.com",
            "test+tag@example.co.uk",
            "test_user@example.org"
        )

        emails.forEach { email ->
            val validateRequest = ValidateOTPRequest(
                email = email,
                otpCode = "123456"
            )

            `when`(mockUserRepository.validateOTP(validateRequest)).thenReturn(mockValidateOTPCall)

            // When
            userViewModel.validateOTP(validateRequest) { _, _, _ -> }

            // Then
            verify(mockUserRepository).validateOTP(validateRequest)
        }
    }

    // ========== HELPER METHODS ==========

    private fun createValidRegisterRequest(): RegisterUserRequest {
        return RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "seller",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = "3001234567",
            doi = "12345678",
            address = "Calle 123 #45-67"
        )
    }

    private fun createValidLoginRequest(): LoginUserRequest {
        return LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
    }

    private fun createValidOTPRequest(): ValidateOTPRequest {
        return ValidateOTPRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            otpCode = "123456"
        )
    }
}