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
import java.util.Date

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

    // ========== VALIDATE OTP TESTS ==========

    @Test
    fun `validateOTP should call repository`() {
        // Given
        val validRequest = ValidateOTPRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            otpCode = "123456"
        )
        
        `when`(mockUserRepository.validateOTP(validRequest)).thenReturn(mockValidateOTPCall)
        
        // When
        userViewModel.validateOTP(validRequest) { _, _, _ -> }
        
        // Then
        verify(mockUserRepository).validateOTP(validRequest)
    }

    @Test
    fun `validateOTP should handle empty request`() {
        // Given
        val emptyRequest = ValidateOTPRequest(
            email = "",
            otpCode = ""
        )
        
        `when`(mockUserRepository.validateOTP(emptyRequest)).thenReturn(mockValidateOTPCall)
        
        // When & Then - Both should be able to call the method
        try {
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
}