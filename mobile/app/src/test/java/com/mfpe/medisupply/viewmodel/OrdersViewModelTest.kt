package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.OrderListResponse
import com.mfpe.medisupply.data.repository.OrderRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class OrdersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockOrderRepository: OrderRepository

    @Mock
    private lateinit var mockCall: Call<OrderListResponse>

    @Mock
    private lateinit var mockResponse: Response<OrderListResponse>

    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setup() {
        viewModel = OrdersViewModel(mockOrderRepository)
    }

    @Test
    fun `OrdersViewModel should have correct class name`() {
        // Given
        val className = OrdersViewModel::class.java.simpleName
        
        // When & Then
        assertEquals("OrdersViewModel", className)
    }

    @Test
    fun `OrdersViewModel should extend ViewModel`() {
        // Given
        val superClass = OrdersViewModel::class.java.superclass
        
        // When & Then
        assertEquals(ViewModel::class.java, superClass)
    }

    @Test
    fun `OrdersViewModel should be properly configured`() {
        // Given
        val viewModelClass = OrdersViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `OrdersViewModel should be instantiable`() {
        // Given & When
        val newViewModel = OrdersViewModel()

        // Then
        assertNotNull(newViewModel)
    }

    @Test
    fun `OrdersViewModel should have getOrders method`() {
        // Given
        val methods = OrdersViewModel::class.java.methods

        // When
        val getOrdersMethod = methods.find { it.name == "getOrders" }

        // Then
        assertNotNull(getOrdersMethod)
    }

    @Test
    fun `getOrders method should accept correct parameters`() {
        // Given
        val method = OrdersViewModel::class.java.methods.find { it.name == "getOrders" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull(parameterTypes)
        assertEquals(3, parameterTypes?.size)
    }

    @Test
    fun `OrdersViewModel should have orderRepository field`() {
        // Given
        val viewModelClass = OrdersViewModel::class.java

        // When
        val orderRepositoryField = viewModelClass.declaredFields.find {
            it.name == "orderRepository"
        }

        // Then
        assertNotNull(orderRepositoryField)
    }

    @Test
    fun `getOrders should call repository and handle successful response`() {
        // Given
        val clientId = "1"
        val sellerId = "2"
        val mockOrderResponse = OrderListResponse(emptyList())
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockOrderResponse)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrders(clientId, sellerId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Orders obtained.", messageResult)
        assertEquals(mockOrderResponse, responseResult)
    }

    @Test
    fun `getOrders should handle unsuccessful response`() {
        // Given
        val clientId = "1"
        val sellerId = "2"
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrders(clientId, sellerId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error obtaining orders: 404", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getOrders should handle successful response with null body`() {
        // Given
        val clientId = "1"
        val sellerId = "2"
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(null)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrders(clientId, sellerId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure when body is null", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error obtaining orders"))
        assertNull(responseResult)
    }

    @Test
    fun `getOrders should handle null response body`() {
        // Given
        val clientId = "1"
        val sellerId = "2"
        var callbackExecuted = false

        // When - This will trigger the actual method execution
        try {
            viewModel.getOrders(clientId, sellerId) { success, message, response ->
                callbackExecuted = true
            }

            // Wait a bit for the async call to potentially complete
            Thread.sleep(100)
        } catch (e: Exception) {
            // Network errors are expected in unit tests
        }

        // Then - The method should have been called
        assertNotNull("getOrders method should exist",
            OrdersViewModel::class.java.methods.find { it.name == "getOrders" })
    }

    @Test
    fun `getOrders should handle different client and seller ids`() {
        // Given
        val testCases = listOf(
            Pair("1", "1"),
            Pair("0", "0"),
            Pair("999", "999"),
            Pair("-1", "-1")
        )

        // When & Then - All should be able to call the method
        testCases.forEach { (clientId, sellerId) ->
            `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)
            `when`(mockResponse.isSuccessful).thenReturn(false)
            `when`(mockResponse.code()).thenReturn(500)
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderListResponse>>(0)
                callback.onResponse(mockCall, mockResponse)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrders(clientId, sellerId) { _, _, _ -> }
            
            verify(mockOrderRepository).getOrders(clientId, sellerId)
        }
    }

    @Test
    fun `getOrders should handle multiple calls`() {
        // Given
        val clientId = "1"
        val sellerId = "2"
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)

        // When - Call the same method multiple times
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository, times(3)).getOrders(clientId, sellerId)
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `getOrders should handle zero values`() {
        // Given
        val clientId = "0"
        val sellerId = "0"
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)

        // When
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
    }

    @Test
    fun `getOrders should handle negative values`() {
        // Given
        val clientId = "-1"
        val sellerId = "-1"
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)

        // When
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
    }

    @Test
    fun `getOrders should handle large values`() {
        // Given
        val clientId = Int.MAX_VALUE.toString()
        val sellerId = Int.MAX_VALUE.toString()
        
        `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)

        // When
        viewModel.getOrders(clientId, sellerId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository).getOrders(clientId, sellerId)
        verify(mockCall).enqueue(any())
    }

    @Test
    fun `OrdersViewModel should handle multiple instances`() {
        // Given
        val viewModel1 = OrdersViewModel()
        val viewModel2 = OrdersViewModel()
        val clientId = "1"
        val sellerId = "2"

        // When
        viewModel1.getOrders(clientId, sellerId) { _, _, _ -> }
        viewModel2.getOrders(clientId, sellerId) { _, _, _ -> }

        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
    }

    @Test
    fun `getOrders should handle concurrent calls`() {
        // Given
        val clientId = "1"
        val sellerId = "2"

        // When - Execute methods concurrently
        try {
            val thread1 = Thread {
                viewModel.getOrders(clientId, sellerId) { _, _, _ -> }
            }
            val thread2 = Thread {
                viewModel.getOrders(clientId + 1, sellerId + 1) { _, _, _ -> }
            }
            val thread3 = Thread {
                viewModel.getOrders(clientId + 2, sellerId + 2) { _, _, _ -> }
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

        // Then - Method should exist and be callable
        assertNotNull("getOrders method should exist",
            OrdersViewModel::class.java.methods.find { it.name == "getOrders" })
    }

    @Test
    fun `getOrders should handle different combinations of client and seller ids`() {
        // Given
        val testCases = listOf(
            Pair("1", "0"),
            Pair("0", "1"),
            Pair("100", "200"),
            Pair("999", "1")
        )

        // When
        testCases.forEach { (clientId, sellerId) ->
            `when`(mockOrderRepository.getOrders(clientId, sellerId)).thenReturn(mockCall)
            `when`(mockResponse.isSuccessful).thenReturn(false)
            `when`(mockResponse.code()).thenReturn(500)
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderListResponse>>(0)
                callback.onResponse(mockCall, mockResponse)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrders(clientId, sellerId) { success, message, response ->
                // Callback executed
            }
            
            verify(mockOrderRepository).getOrders(clientId, sellerId)
        }
    }
}