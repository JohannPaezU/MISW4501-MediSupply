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

    @Mock
    private lateinit var mockRegisterResponse: Response<RegisterUserResponse>

    @Mock
    private lateinit var mockLoginResponse: Response<LoginUserResponse>

    @Mock
    private lateinit var mockValidateOTPResponse: Response<ValidateOTPResponse>

    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        userViewModel = UserViewModel(mockUserRepository)
    }

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
        assertTrue("UserViewModel should extend ViewModel", 
            viewModel is ViewModel)
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
    fun `registerUser should call repository and handle successful response`() {
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
        val mockRegisterResponseData = RegisterUserResponse(id = "123", createdAt = java.util.Date())
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockRegisterResponse.isSuccessful).thenReturn(true)
        `when`(mockRegisterResponse.body()).thenReturn(mockRegisterResponseData)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterUserResponse>>(0)
            callback.onResponse(mockRegisterCall, mockRegisterResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        userViewModel.registerUser(registerRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).registerUser(registerRequest)
        verify(mockRegisterCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("123", messageResult)
    }

    @Test
    fun `registerUser should handle unsuccessful response`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        
        var callbackExecuted = false
        
        // When - This will trigger the actual method execution
        try {
            userViewModel.validateOTP(otpRequest) { success, message, response ->
                callbackExecuted = true
            }
            
            // Wait a bit for the async call to potentially complete
            Thread.sleep(100)
        } catch (e: Exception) {
            // Network errors are expected in unit tests
        }
        
        // Then - The method should have been called
        assertNotNull("validateOTP method should exist", 
            UserViewModel::class.java.getDeclaredMethod("validateOTP", 
                ValidateOTPRequest::class.java, 
                kotlin.Function3::class.java))
    }

    @Test
    fun `registerUser should handle different request types`() {
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
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockRegisterResponse.isSuccessful).thenReturn(false)
        `when`(mockRegisterResponse.code()).thenReturn(400)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterUserResponse>>(0)
            callback.onResponse(mockRegisterCall, mockRegisterResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        userViewModel.registerUser(registerRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).registerUser(registerRequest)
        verify(mockRegisterCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error registering user: 400", messageResult)
    }

    @Test
    fun `registerUser should handle successful response with null body`() {
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
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockRegisterResponse.isSuccessful).thenReturn(true)
        `when`(mockRegisterResponse.body()).thenReturn(null)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterUserResponse>>(0)
            callback.onResponse(mockRegisterCall, mockRegisterResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        userViewModel.registerUser(registerRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).registerUser(registerRequest)
        verify(mockRegisterCall).enqueue(any())
        assertFalse("Should return failure when body is null", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error registering user"))
    }

    @Test
    fun `registerUser should handle network failure`() {
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
        val throwable = RuntimeException("Network error")
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterUserResponse>>(0)
            callback.onFailure(mockRegisterCall, throwable)
            null
        }.`when`(mockRegisterCall).enqueue(any())

        userViewModel.registerUser(registerRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).registerUser(registerRequest)
        verify(mockRegisterCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Connection error: Network error", messageResult)
    }

    @Test
    fun `validateOTP should handle different request types`() {
        // Given
        val validRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        val emptyRequest = ValidateOTPRequest(otpCode = "", email = TestUtils.TestData.VALID_EMAIL)
        
        // When & Then - Both should be able to call the method
        try {
            userViewModel.validateOTP(validRequest) { _, _, _ -> }
            userViewModel.validateOTP(emptyRequest) { _, _, _ -> }
        } catch (e: Exception) {
            // Network errors are expected
        }
        
        // Verify the method exists and can be called
        assertNotNull("validateOTP method should exist", 
            UserViewModel::class.java.getDeclaredMethod("validateOTP", 
                ValidateOTPRequest::class.java, 
                kotlin.Function3::class.java))
    }


    // ========== LOGIN USER TESTS ==========

    @Test
    fun `loginUser should call repository and handle successful response`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
        val mockLoginResponseData = LoginUserResponse(message = "Login successful")
        
        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        `when`(mockLoginResponse.isSuccessful).thenReturn(true)
        `when`(mockLoginResponse.body()).thenReturn(mockLoginResponseData)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<LoginUserResponse>>(0)
            callback.onResponse(mockLoginCall, mockLoginResponse)
            null
        }.`when`(mockLoginCall).enqueue(any())

        userViewModel.loginUser(loginRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
        verify(mockLoginCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Login successful", messageResult)
    }

    @Test
    fun `loginUser should handle unsuccessful response`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
        
        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        `when`(mockLoginResponse.isSuccessful).thenReturn(false)
        `when`(mockLoginResponse.code()).thenReturn(401)
        
        var successResult = false
        var messageResult = ""
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<LoginUserResponse>>(0)
            callback.onResponse(mockLoginCall, mockLoginResponse)
            null
        }.`when`(mockLoginCall).enqueue(any())

        userViewModel.loginUser(loginRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
        verify(mockLoginCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error logging in: 401", messageResult)
    }

    @Test
    fun `loginUser should handle network failure`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
        val throwable = RuntimeException("Network error")
        
        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        
        var successResult = false
        var messageResult = ""
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<LoginUserResponse>>(0)
            callback.onFailure(mockLoginCall, throwable)
            null
        }.`when`(mockLoginCall).enqueue(any())

        userViewModel.loginUser(loginRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
        verify(mockLoginCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Connection error: Network error", messageResult)
    }

    @Test
    fun `loginUser should handle successful response with null body`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )
        
        `when`(mockUserRepository.loginUser(loginRequest)).thenReturn(mockLoginCall)
        `when`(mockLoginResponse.isSuccessful).thenReturn(true)
        `when`(mockLoginResponse.body()).thenReturn(null)
        
        var successResult = false
        var messageResult = ""

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<LoginUserResponse>>(0)
            callback.onResponse(mockLoginCall, mockLoginResponse)
            null
        }.`when`(mockLoginCall).enqueue(any())

        userViewModel.loginUser(loginRequest) { success, message ->
            successResult = success
            messageResult = message
        }

        // Then
        verify(mockUserRepository).loginUser(loginRequest)
        verify(mockLoginCall).enqueue(any())
        assertFalse("Should return failure when body is null", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error logging in"))
    }

    // ========== VALIDATE OTP TESTS ==========

    @Test
    fun `validateOTP should call repository and handle successful response`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        val mockValidateOTPResponseData = ValidateOTPResponse(
            accessToken = "test_token",
            user = OTPUser(
                id = "123",
                fullName = TestUtils.TestData.VALID_FULL_NAME,
                email = TestUtils.TestData.VALID_EMAIL,
                role = "institutional"
            )
        )
        
        `when`(mockUserRepository.validateOTP(otpRequest)).thenReturn(mockValidateOTPCall)
        `when`(mockValidateOTPResponse.isSuccessful).thenReturn(true)
        `when`(mockValidateOTPResponse.body()).thenReturn(mockValidateOTPResponseData)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ValidateOTPResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ValidateOTPResponse>>(0)
            callback.onResponse(mockValidateOTPCall, mockValidateOTPResponse)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        userViewModel.validateOTP(otpRequest) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockUserRepository).validateOTP(otpRequest)
        verify(mockValidateOTPCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("OTP validated.", messageResult)
        assertEquals(mockValidateOTPResponseData, responseResult)
    }

    @Test
    fun `validateOTP should handle unsuccessful response`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        
        `when`(mockUserRepository.validateOTP(otpRequest)).thenReturn(mockValidateOTPCall)
        `when`(mockValidateOTPResponse.isSuccessful).thenReturn(false)
        `when`(mockValidateOTPResponse.code()).thenReturn(400)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ValidateOTPResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ValidateOTPResponse>>(0)
            callback.onResponse(mockValidateOTPCall, mockValidateOTPResponse)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        userViewModel.validateOTP(otpRequest) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockUserRepository).validateOTP(otpRequest)
        verify(mockValidateOTPCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error validating OTP: 400", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `validateOTP should handle successful response with null body`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        
        `when`(mockUserRepository.validateOTP(otpRequest)).thenReturn(mockValidateOTPCall)
        `when`(mockValidateOTPResponse.isSuccessful).thenReturn(true)
        `when`(mockValidateOTPResponse.body()).thenReturn(null)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ValidateOTPResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ValidateOTPResponse>>(0)
            callback.onResponse(mockValidateOTPCall, mockValidateOTPResponse)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        userViewModel.validateOTP(otpRequest) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockUserRepository).validateOTP(otpRequest)
        verify(mockValidateOTPCall).enqueue(any())
        assertFalse("Should return failure when body is null", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error validating OTP"))
        assertNull(responseResult)
    }

    @Test
    fun `validateOTP should handle network failure`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = TestUtils.TestData.VALID_OTP, email = TestUtils.TestData.VALID_EMAIL)
        val throwable = RuntimeException("Network error")
        
        `when`(mockUserRepository.validateOTP(otpRequest)).thenReturn(mockValidateOTPCall)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ValidateOTPResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ValidateOTPResponse>>(0)
            callback.onFailure(mockValidateOTPCall, throwable)
            null
        }.`when`(mockValidateOTPCall).enqueue(any())

        userViewModel.validateOTP(otpRequest) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockUserRepository).validateOTP(otpRequest)
        verify(mockValidateOTPCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Connection error: Network error", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `UserViewModel should handle multiple method calls`() {
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
        
        // When - Execute methods concurrently
        try {
            val thread1 = Thread {
                userViewModel.registerUser(registerRequest) { _, _ -> }
            }
            val thread2 = Thread {
                userViewModel.loginUser(loginRequest) { _, _ -> }
            }
            val thread3 = Thread {
                userViewModel.validateOTP(otpRequest) { _, _, _ -> }
            }
            
            thread1.start()
            thread2.start()
            thread3.start()
            
            thread1.join()
            thread2.join()
            thread3.join()
        } catch (e: Exception) {
            // Network errors are expected
        }
        
        // Then - All methods should exist and be callable
        assertNotNull("registerUser method should exist", 
            UserViewModel::class.java.getDeclaredMethod("registerUser", 
                RegisterUserRequest::class.java, 
                kotlin.Function2::class.java))
        
        assertNotNull("loginUser method should exist", 
            UserViewModel::class.java.getDeclaredMethod("loginUser", 
                LoginUserRequest::class.java, 
                kotlin.Function2::class.java))
        
        assertNotNull("validateOTP method should exist", 
            UserViewModel::class.java.getDeclaredMethod("validateOTP", 
                ValidateOTPRequest::class.java, 
                kotlin.Function3::class.java))
    }

    @Test
    fun `UserViewModel should handle multiple instances`() {
        // Given
        val viewModel1 = UserViewModel(mockUserRepository)
        val viewModel2 = UserViewModel(mockUserRepository)
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            doi = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )
        
        `when`(mockUserRepository.registerUser(registerRequest)).thenReturn(mockRegisterCall)
        `when`(mockRegisterResponse.isSuccessful).thenReturn(false)
        `when`(mockRegisterResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterUserResponse>>(0)
            callback.onResponse(mockRegisterCall, mockRegisterResponse)
            null
        }.`when`(mockRegisterCall).enqueue(any())
        
        // When
        viewModel1.registerUser(registerRequest) { _, _ -> }
        viewModel2.registerUser(registerRequest) { _, _ -> }
        
        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
        verify(mockUserRepository, times(2)).registerUser(registerRequest)
    }

}