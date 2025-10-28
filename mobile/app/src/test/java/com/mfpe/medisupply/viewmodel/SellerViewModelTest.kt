package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.SellerHomeResponse
import com.mfpe.medisupply.data.repository.SellerRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class SellerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockSellerRepository: SellerRepository

    @Mock
    private lateinit var mockCall: Call<SellerHomeResponse>

    @Mock
    private lateinit var mockResponse: Response<SellerHomeResponse>

    private lateinit var viewModel: SellerViewModel

    @Before
    fun setUp() {
        viewModel = SellerViewModel(mockSellerRepository)
    }

    @Test
    fun `SellerViewModel should have correct class name`() {
        // Given
        val className = SellerViewModel::class.java.simpleName
        
        // When & Then
        assertEquals("SellerViewModel", className)
    }

    @Test
    fun `SellerViewModel should extend ViewModel`() {
        // Given
        val superClass = SellerViewModel::class.java.superclass
        
        // When & Then
        assertEquals(ViewModel::class.java, superClass)
    }

    @Test
    fun `SellerViewModel should be properly configured`() {
        // Given
        val viewModelClass = SellerViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `SellerViewModel should be instantiable`() {
        // Given & When
        val newViewModel = SellerViewModel()

        // Then
        assertNotNull(newViewModel)
    }

    @Test
    fun `SellerViewModel should have getHome method`() {
        // Given
        val methods = SellerViewModel::class.java.methods

        // When
        val getHomeMethod = methods.find { it.name == "getHome" }

        // Then
        assertNotNull(getHomeMethod)
    }

    @Test
    fun `getHome method should accept correct parameters`() {
        // Given
        val method = SellerViewModel::class.java.methods.find { it.name == "getHome" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull(parameterTypes)
        assertEquals(2, parameterTypes?.size)
    }

    @Test
    fun `SellerViewModel should have sellerRepository field`() {
        // Given
        val viewModelClass = SellerViewModel::class.java

        // When
        val sellerRepositoryField = viewModelClass.declaredFields.find {
            it.name == "sellerRepository"
        }

        // Then
        assertNotNull(sellerRepositoryField)
    }

    @Test
    fun `getHome should call repository and handle successful response`() {
        // Given
        val mockSellerResponse = SellerHomeResponse(
            id = 1,
            numberClients = 5,
            numberOrders = 10,
            vendorZone = "Zona 1"
        )
        
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockSellerResponse)
        
        var successResult = false
        var messageResult = ""
        var responseResult: SellerHomeResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getHome("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Seller home obtained.", messageResult)
        assertEquals(mockSellerResponse, responseResult)
    }

    @Test
    fun `getHome should handle unsuccessful response`() {
        // Given
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        
        var successResult = false
        var messageResult = ""
        var responseResult: SellerHomeResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getHome("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error obtaining seller home: 404", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getHome should handle null response body`() {
        // Given
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(null)
        `when`(mockResponse.code()).thenReturn(200)
        
        var successResult = false
        var messageResult = ""
        var responseResult: SellerHomeResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getHome("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error obtaining seller home: 200", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getHome should handle network failure`() {
        // Given
        val throwable = RuntimeException("Network error")
        
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        
        var successResult = false
        var messageResult = ""
        var responseResult: SellerHomeResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onFailure(mockCall, throwable)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getHome("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Connection error: Network error", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getHome should handle multiple calls`() {
        // Given
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When - Call the same method multiple times
        viewModel.getHome("") { _, _, _ -> }
        viewModel.getHome("") { _, _, _ -> }
        viewModel.getHome("") { _, _, _ -> }

        // Then
        verify(mockSellerRepository, times(3)).getHome("")
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `SellerViewModel should handle multiple instances`() {
        // Given
        val viewModel1 = SellerViewModel(mockSellerRepository)
        val viewModel2 = SellerViewModel(mockSellerRepository)
        
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        viewModel1.getHome("") { _, _, _ -> }
        viewModel2.getHome("") { _, _, _ -> }

        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
        verify(mockSellerRepository, times(2)).getHome("")
    }

    @Test
    fun `getHome should handle concurrent calls`() {
        // Given
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When - Execute methods concurrently
        val thread1 = Thread {
            viewModel.getHome("") { _, _, _ -> }
        }
        val thread2 = Thread {
            viewModel.getHome("") { _, _, _ -> }
        }
        val thread3 = Thread {
            viewModel.getHome("") { _, _, _ -> }
        }

        thread1.start()
        thread2.start()
        thread3.start()

        thread1.join()
        thread2.join()
        thread3.join()

        // Then - Method should exist and be callable
        verify(mockSellerRepository, times(3)).getHome("")
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `getHome should handle different callback scenarios`() {
        // Given
        var successCallbackCount = 0
        var failureCallbackCount = 0
        
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        viewModel.getHome("") { success, message, response ->
            if (success) {
                successCallbackCount++
            } else {
                failureCallbackCount++
            }
        }

        // Then - Method should have been called
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
        assertEquals(0, successCallbackCount)
        assertEquals(1, failureCallbackCount)
    }

    @Test
    fun `getHome should maintain method signature`() {
        // Given
        val method = SellerViewModel::class.java.methods.find { it.name == "getHome" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull("getHome method should exist", method)
        assertNotNull("Parameter types should not be null", parameterTypes)
        assertEquals("Should have 2 parameter", 2, parameterTypes?.size)
    }

    @Test
    fun `SellerViewModel should handle rapid instantiation and calls`() {
        // Given & When
        val viewModels = (1..5).map { SellerViewModel(mockSellerRepository) }
        
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())
        
        viewModels.forEach { vm ->
            vm.getHome("") { _, _, _ -> }
        }

        // Then
        assertEquals("Should create 5 viewModels", 5, viewModels.size)
        viewModels.forEach { vm ->
            assertNotNull("Each viewModel should not be null", vm)
        }
        verify(mockSellerRepository, times(5)).getHome("")
    }

    @Test
    fun `getHome should handle callback with null response`() {
        // Given
        `when`(mockSellerRepository.getHome("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<SellerHomeResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        viewModel.getHome("") { success, message, response ->
            // Callback should handle null response gracefully
            if (success) {
                assertNotNull("Response should not be null on success", response)
            } else {
                assertNull("Response should be null on failure", response)
            }
        }

        // Then
        verify(mockSellerRepository).getHome("")
        verify(mockCall).enqueue(any())
    }
}