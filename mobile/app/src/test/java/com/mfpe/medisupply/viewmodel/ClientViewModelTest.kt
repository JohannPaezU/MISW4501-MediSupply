package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.repository.ClientRepository
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
class ClientViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockClientRepository: ClientRepository

    @Mock
    private lateinit var mockGetClientsCall: Call<ClientListResponse>

    @Mock
    private lateinit var mockGetClientsResponse: Response<ClientListResponse>

    private lateinit var clientViewModel: ClientViewModel

    @Before
    fun setUp() {
        clientViewModel = ClientViewModel(mockClientRepository)
    }

    @Test
    fun `ClientViewModel should be created successfully`() {
        // Given & When
        val viewModel = ClientViewModel()
        
        // Then
        assertNotNull(viewModel)
    }

    @Test
    fun `ClientViewModel should have correct class name`() {
        // Given
        val viewModel = ClientViewModel()
        
        // When & Then
        assertEquals("ClientViewModel", viewModel.javaClass.simpleName)
    }

    @Test
    fun `ClientViewModel should extend ViewModel`() {
        // Given
        val viewModel = ClientViewModel()
        
        // When & Then
        assertTrue("ClientViewModel should extend ViewModel", 
            viewModel is ViewModel)
    }

    @Test
    fun `ClientViewModel should be properly configured`() {
        // Given
        val viewModelClass = ClientViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `ClientViewModel should have getClients method`() {
        // Given
        val viewModelClass = ClientViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("getClients", 
            String::class.java, 
            kotlin.Function3::class.java))
    }

    // ========== GET CLIENTS TESTS ==========

    @Test
    fun `getClients should call repository`() {
        // Given
        val authToken = "Bearer test-token"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }

    @Test
    fun `getClients should handle successful response`() {
        // Given
        val authToken = "Bearer test-token"
        val mockClientsResponse = ClientListResponse(emptyList())
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        `when`(mockGetClientsResponse.isSuccessful).thenReturn(true)
        `when`(mockGetClientsResponse.body()).thenReturn(mockClientsResponse)
        
        var successResult = false
        var messageResult = ""
        var responseData: ClientListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ClientListResponse>>(0)
            callback.onResponse(mockGetClientsCall, mockGetClientsResponse)
            null
        }.`when`(mockGetClientsCall).enqueue(any())

        clientViewModel.getClients(authToken) { success, message, response ->
            successResult = success
            messageResult = message
            responseData = response
        }

        // Then
        verify(mockClientRepository).getClients(authToken)
        verify(mockGetClientsCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Clients obtained.", messageResult)
        assertNotNull("Response should not be null", responseData)
    }

    @Test
    fun `getClients should handle unsuccessful response`() {
        // Given
        val authToken = "Bearer test-token"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        `when`(mockGetClientsResponse.isSuccessful).thenReturn(false)
        `when`(mockGetClientsResponse.code()).thenReturn(404)
        
        var successResult = true
        var messageResult = ""
        var responseData: ClientListResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ClientListResponse>>(0)
            callback.onResponse(mockGetClientsCall, mockGetClientsResponse)
            null
        }.`when`(mockGetClientsCall).enqueue(any())

        clientViewModel.getClients(authToken) { success, message, response ->
            successResult = success
            messageResult = message
            responseData = response
        }

        // Then
        verify(mockClientRepository).getClients(authToken)
        verify(mockGetClientsCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error obtaining clients"))
        assertNull("Response should be null", responseData)
    }

    @Test
    fun `getClients should handle network failure`() {
        // Given
        val authToken = "Bearer test-token"
        val exception = RuntimeException("Network error")
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        var successResult = true
        var messageResult = ""
        var responseData: ClientListResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ClientListResponse>>(0)
            callback.onFailure(mockGetClientsCall, exception)
            null
        }.`when`(mockGetClientsCall).enqueue(any())

        clientViewModel.getClients(authToken) { success, message, response ->
            successResult = success
            messageResult = message
            responseData = response
        }

        // Then
        verify(mockClientRepository).getClients(authToken)
        verify(mockGetClientsCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertTrue("Should contain connection error message", messageResult.contains("Connection error"))
        assertNull("Response should be null", responseData)
    }

    @Test
    fun `getClients should handle successful response with null body`() {
        // Given
        val authToken = "Bearer test-token"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        `when`(mockGetClientsResponse.isSuccessful).thenReturn(true)
        `when`(mockGetClientsResponse.body()).thenReturn(null)
        
        var successResult = true
        var messageResult = ""
        var responseData: ClientListResponse? = null
        
        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ClientListResponse>>(0)
            callback.onResponse(mockGetClientsCall, mockGetClientsResponse)
            null
        }.`when`(mockGetClientsCall).enqueue(any())

        clientViewModel.getClients(authToken) { success, message, response ->
            successResult = success
            messageResult = message
            responseData = response
        }

        // Then
        verify(mockClientRepository).getClients(authToken)
        verify(mockGetClientsCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error obtaining clients"))
        assertNull("Response should be null", responseData)
    }

    @Test
    fun `getClients should handle empty auth token`() {
        // Given
        val authToken = ""
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }

    @Test
    fun `getClients should handle valid auth token`() {
        // Given
        val authToken = "Bearer test-token"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }

    @Test
    fun `getClients should handle special characters in auth token`() {
        // Given
        val authToken = "Bearer test-token-with-special-chars-123"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }

    @Test
    fun `getClients should handle numeric auth token`() {
        // Given
        val authToken = "Bearer 12345"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }

    @Test
    fun `getClients should handle long auth token`() {
        // Given
        val authToken = "Bearer very-long-token-that-might-be-used-in-some-systems-with-many-characters-and-special-symbols"
        val sellerId = "seller-123"
        
        `when`(mockClientRepository.getClients(authToken)).thenReturn(mockGetClientsCall)
        
        // When
        clientViewModel.getClients(authToken) { _, _, _ -> }
        
        // Then
        verify(mockClientRepository).getClients(authToken)
    }
}
