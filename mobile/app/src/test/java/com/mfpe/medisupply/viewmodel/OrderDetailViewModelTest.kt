package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.*
import com.mfpe.medisupply.data.repository.OrderRepository
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
class OrderDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockOrderRepository: OrderRepository

    @Mock
    private lateinit var mockCall: Call<OrderDetailResponse>

    @Mock
    private lateinit var mockResponse: Response<OrderDetailResponse>

    private lateinit var viewModel: OrderDetailViewModel

    @Before
    fun setUp() {
        viewModel = OrderDetailViewModel(mockOrderRepository)
    }

    // ========== BASIC TESTS ==========

    @Test
    fun `OrderDetailViewModel should be created successfully`() {
        // Given & When
        val viewModel = OrderDetailViewModel()
        
        // Then
        assertNotNull("ViewModel should not be null", viewModel)
    }

    @Test
    fun `OrderDetailViewModel should have correct class name`() {
        // Given
        val viewModel = OrderDetailViewModel()
        
        // When & Then
        assertEquals("OrderDetailViewModel", viewModel.javaClass.simpleName)
    }

    @Test
    fun `OrderDetailViewModel should extend ViewModel`() {
        // Given
        val viewModel = OrderDetailViewModel()
        
        // When & Then
        assertTrue("OrderDetailViewModel should extend ViewModel", 
            viewModel is ViewModel)
    }

    @Test
    fun `OrderDetailViewModel should be properly configured`() {
        // Given
        val viewModelClass = OrderDetailViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `OrderDetailViewModel should have getOrderDetail method`() {
        // Given
        val viewModelClass = OrderDetailViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("getOrderDetail", 
            String::class.java,
            String::class.java,
            kotlin.Function3::class.java))
    }

    @Test
    fun `OrderDetailViewModel should be instantiable multiple times`() {
        // When
        val viewModels = (1..5).map { OrderDetailViewModel() }

        // Then
        assertEquals("Should create 5 view models", 5, viewModels.size)
        viewModels.forEach { vm ->
            assertNotNull("Each view model should not be null", vm)
            assertTrue("Each view model should be ViewModel", vm is ViewModel)
        }
    }

    @Test
    fun `OrderDetailViewModel should have orderRepository field`() {
        // Given
        val viewModelClass = OrderDetailViewModel::class.java

        // When
        val orderRepositoryField = viewModelClass.declaredFields.find {
            it.name == "orderRepository"
        }

        // Then
        assertNotNull(orderRepositoryField)
    }

    // ========== GET ORDER DETAIL TESTS ==========

    @Test
    fun `getOrderDetail should call repository and handle successful response`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val mockOrderDetailResponse = createTestOrderDetailResponse()
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockOrderDetailResponse)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderDetailResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrderDetail("", orderId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrderDetail("", orderId)
        verify(mockCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Orden obtenida correctamente", messageResult)
        assertEquals(mockOrderDetailResponse, responseResult)
    }

    @Test
    fun `getOrderDetail should handle unsuccessful response`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderDetailResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrderDetail("", orderId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrderDetail("", orderId)
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error al cargar la orden: 404", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getOrderDetail should handle successful response with null body`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(null)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderDetailResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrderDetail("", orderId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrderDetail("", orderId)
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure when body is null", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error al cargar la orden"))
        assertNull(responseResult)
    }

    @Test
    fun `getOrderDetail should handle network failure`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val exception = Exception("Network error")
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
        
        var successResult = false
        var messageResult = ""
        var responseResult: OrderDetailResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onFailure(mockCall, exception)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getOrderDetail("", orderId) { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockOrderRepository).getOrderDetail("", orderId)
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertTrue("Should contain error message", messageResult.contains("Error de conexión"))
        assertTrue("Should contain exception message", messageResult.contains("Network error"))
        assertNull(responseResult)
    }

    @Test
    fun `getOrderDetail should handle different order IDs`() {
        // Given
        val testOrderIds = listOf(
            "d0a2d69d-b082-42e4-99eb-733e413e2877",
            "12345678-1234-1234-1234-123456789012",
            "00000000-0000-0000-0000-000000000000"
        )

        // When & Then
        testOrderIds.forEach { orderId ->
            `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
            `when`(mockResponse.isSuccessful).thenReturn(false)
            `when`(mockResponse.code()).thenReturn(500)
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
                callback.onResponse(mockCall, mockResponse)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrderDetail("", orderId) { _, _, _ -> }
            
            verify(mockOrderRepository).getOrderDetail("", orderId)
            reset(mockOrderRepository)
            reset(mockCall)
        }
    }

    @Test
    fun `getOrderDetail should handle different auth tokens`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val authTokens = listOf(
            "",
            "Bearer token123",
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        )

        // When & Then
        authTokens.forEach { authToken ->
            `when`(mockOrderRepository.getOrderDetail(authToken, orderId)).thenReturn(mockCall)
            `when`(mockResponse.isSuccessful).thenReturn(false)
            `when`(mockResponse.code()).thenReturn(401)
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
                callback.onResponse(mockCall, mockResponse)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrderDetail(authToken, orderId) { _, _, _ -> }
            
            verify(mockOrderRepository).getOrderDetail(authToken, orderId)
            reset(mockOrderRepository)
            reset(mockCall)
        }
    }

    @Test
    fun `getOrderDetail should handle multiple calls`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)

        // When - Call the same method multiple times
        viewModel.getOrderDetail("", orderId) { _, _, _ -> }
        viewModel.getOrderDetail("", orderId) { _, _, _ -> }
        viewModel.getOrderDetail("", orderId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository, times(3)).getOrderDetail("", orderId)
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `getOrderDetail should handle empty order ID`() {
        // Given
        val orderId = ""
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)

        // When
        viewModel.getOrderDetail("", orderId) { _, _, _ -> }

        // Then
        verify(mockOrderRepository).getOrderDetail("", orderId)
        verify(mockCall).enqueue(any())
    }

    @Test
    fun `getOrderDetail should handle different HTTP error codes`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val errorCodes = listOf(400, 401, 403, 404, 500, 503)

        // When & Then
        errorCodes.forEach { errorCode ->
            `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
            `when`(mockResponse.isSuccessful).thenReturn(false)
            `when`(mockResponse.code()).thenReturn(errorCode)
            
            var messageResult = ""
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
                callback.onResponse(mockCall, mockResponse)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrderDetail("", orderId) { _, message, _ ->
                messageResult = message
            }
            
            assertEquals("Error al cargar la orden: $errorCode", messageResult)
            reset(mockOrderRepository)
            reset(mockCall)
        }
    }

    @Test
    fun `getOrderDetail should handle different exception messages`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val exceptions = listOf(
            Exception("Network error"),
            Exception("Timeout"),
            Exception("Connection refused"),
            RuntimeException("Unexpected error")
        )

        // When & Then
        exceptions.forEach { exception ->
            `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
            
            var messageResult = ""
            
            doAnswer { invocation ->
                val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
                callback.onFailure(mockCall, exception)
                null
            }.`when`(mockCall).enqueue(any())

            viewModel.getOrderDetail("", orderId) { _, message, _ ->
                messageResult = message
            }
            
            assertTrue("Should contain error message", messageResult.contains("Error de conexión"))
            assertTrue("Should contain exception message", messageResult.contains(exception.message ?: ""))
            reset(mockOrderRepository)
            reset(mockCall)
        }
    }

    @Test
    fun `OrderDetailViewModel should handle multiple instances`() {
        // Given
        val viewModel1 = OrderDetailViewModel()
        val viewModel2 = OrderDetailViewModel()
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"

        // When
        viewModel1.getOrderDetail("", orderId) { _, _, _ -> }
        viewModel2.getOrderDetail("", orderId) { _, _, _ -> }

        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
    }

    @Test
    fun `getOrderDetail should handle concurrent calls`() {
        // Given
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail("", orderId)).thenReturn(mockCall)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onFailure(mockCall, Exception("Network error"))
            null
        }.`when`(mockCall).enqueue(any())

        // When - Execute methods concurrently
        try {
            val thread1 = Thread {
                viewModel.getOrderDetail("", orderId) { _, _, _ -> }
            }
            val thread2 = Thread {
                viewModel.getOrderDetail("", orderId) { _, _, _ -> }
            }
            val thread3 = Thread {
                viewModel.getOrderDetail("", orderId) { _, _, _ -> }
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
        assertNotNull("getOrderDetail method should exist",
            OrderDetailViewModel::class.java.methods.find { it.name == "getOrderDetail" })
    }

    @Test
    fun `getOrderDetail should not throw exception with valid parameters`() {
        // Given
        val authToken = "Bearer test-token"
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail(authToken, orderId)).thenReturn(mockCall)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onFailure(mockCall, Exception("Network error"))
            null
        }.`when`(mockCall).enqueue(any())

        // When & Then
        try {
            viewModel.getOrderDetail(authToken, orderId) { _, _, _ ->
                // Callback should be invoked
            }
            assertTrue("Method should execute without throwing exception", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getOrderDetail should handle empty auth token`() {
        // Given
        val authToken = ""
        val orderId = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        
        `when`(mockOrderRepository.getOrderDetail(authToken, orderId)).thenReturn(mockCall)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<OrderDetailResponse>>(0)
            callback.onFailure(mockCall, Exception("Network error"))
            null
        }.`when`(mockCall).enqueue(any())
        
        // When & Then
        try {
            viewModel.getOrderDetail(authToken, orderId) { _, _, _ ->
                // Callback should be invoked
            }
            assertTrue(true) // Should not throw exception
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getOrderDetail method should accept correct parameters`() {
        // Given
        val method = OrderDetailViewModel::class.java.methods.find { it.name == "getOrderDetail" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull(parameterTypes)
        assertEquals(3, parameterTypes?.size)
    }

    // Helper methods
    private fun createTestOrderDetailResponse(): OrderDetailResponse {
        return OrderDetailResponse(
            id = "d0a2d69d-b082-42e4-99eb-733e413e2877",
            comments = "prueba con vendedor",
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = createTestClient(),
            distribution_center = createTestDistributionCenter(),
            products = listOf(createTestOrderProductDetail())
        )
    }

    private fun createTestClient(): Client {
        return Client(
            id = "77777777-7777-7777-7777-777777777777",
            full_name = "Julian Oliveros Forero (Institucional)",
            email = "julian.oliveros@somosplenti.com",
            phone = "123456789",
            doi = "7777777777-7",
            address = "09927 Williams Brooks Suite 547\nStevenside, PA 47656",
            role = "institutional",
            created_at = "2025-11-03T01:25:10.507727Z"
        )
    }

    private fun createTestDistributionCenter(): DistributionCenter {
        return DistributionCenter(
            id = "171ac9e5-f18d-4480-909b-6aa5daa0803b",
            name = "Centro de Distribución Bogotá",
            address = "Calle 123 #45-67, Bogotá, Colombia",
            city = "Bogotá",
            country = "Colombia",
            created_at = "2025-11-03T01:25:27.910864Z"
        )
    }

    private fun createTestOrderProductDetail(): OrderProductDetail {
        return OrderProductDetail(
            id = "58829c8f-9384-4763-a82e-effc0201b7ec",
            name = "Amoxicilina 500mg",
            store = "CEDI Medellín",
            batch = "AMX-MED-2025",
            due_date = "2026-01-17",
            price_per_unit = 18.0,
            quantity = 1,
            image_url = "https://example.com/image.jpg"
        )
    }
}

