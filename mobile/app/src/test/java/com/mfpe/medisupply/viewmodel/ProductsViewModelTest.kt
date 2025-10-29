package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.repository.ProductRepository
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
class ProductsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockProductRepository: ProductRepository

    @Mock
    private lateinit var mockCall: Call<ProductListResponse>

    @Mock
    private lateinit var mockResponse: Response<ProductListResponse>

    private lateinit var viewModel: ProductsViewModel

    @Before
    fun setup() {
        viewModel = ProductsViewModel(mockProductRepository)
    }

    @Test
    fun `ProductsViewModel should have correct class name`() {
        // Given
        val className = ProductsViewModel::class.java.simpleName
        
        // When & Then
        assertEquals("ProductsViewModel", className)
    }

    @Test
    fun `ProductsViewModel should extend ViewModel`() {
        // Given
        val superClass = ProductsViewModel::class.java.superclass
        
        // When & Then
        assertEquals(ViewModel::class.java, superClass)
    }

    @Test
    fun `ProductsViewModel should be properly configured`() {
        // Given
        val viewModelClass = ProductsViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `ProductsViewModel should be instantiable`() {
        // Given & When
        val newViewModel = ProductsViewModel()

        // Then
        assertNotNull(newViewModel)
    }

    @Test
    fun `ProductsViewModel should have getProducts method`() {
        // Given
        val methods = ProductsViewModel::class.java.methods

        // When
        val getProductsMethod = methods.find { it.name == "getProducts" }

        // Then
        assertNotNull(getProductsMethod)
    }

    @Test
    fun `ProductsViewModel should have getCurrentProducts method`() {
        // Given
        val methods = ProductsViewModel::class.java.methods

        // When
        val getCurrentProductsMethod = methods.find { it.name == "getCurrentProducts" }

        // Then
        assertNotNull(getCurrentProductsMethod)
    }

    @Test
    fun `getCurrentProducts should return empty list initially`() {
        // Given & When
        val products = viewModel.getCurrentProducts()

        // Then
        assertNotNull(products)
        assertTrue(products.isEmpty())
    }

    @Test
    fun `ProductsViewModel should have productRepository field`() {
        // Given
        val viewModelClass = ProductsViewModel::class.java

        // When
        val productRepositoryField = viewModelClass.declaredFields.find {
            it.name == "productRepository"
        }

        // Then
        assertNotNull(productRepositoryField)
    }

    @Test
    fun `ProductsViewModel should have currentProducts field`() {
        // Given
        val viewModelClass = ProductsViewModel::class.java

        // When
        val currentProductsField = viewModelClass.declaredFields.find {
            it.name == "currentProducts"
        }

        // Then
        assertNotNull(currentProductsField)
    }

    @Test
    fun `getProducts method should accept correct parameters`() {
        // Given
        val method = ProductsViewModel::class.java.methods.find { it.name == "getProducts" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull(parameterTypes)
        assertEquals(2, parameterTypes?.size)
    }

    @Test
    fun `getProducts should call repository and handle successful response`() {
        // Given
        val now = java.util.Date()
        val mockProduct = Product(
            id = "p1",
            name = "Test Product",
            details = "Test Details",
            store = "Test Store",
            batch = "L1",
            image_url = "test-url",
            due_date = "2026-09-24",
            stock = 5,
            price_per_unit = 10.0,
            created_at = "2025-10-23T05:44:07.144451Z"
        )
        val mockProductListResponse = ProductListResponse(products = listOf(mockProduct))
        
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockProductListResponse)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ProductListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getProducts("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockProductRepository).getProducts("")
        verify(mockCall).enqueue(any())
        assertTrue("Should return success", successResult)
        assertEquals("Products obtained.", messageResult)
        assertEquals(mockProductListResponse, responseResult)
        
        // Verify that currentProducts was updated
        val currentProducts = viewModel.getCurrentProducts()
        assertEquals(1, currentProducts.size)
        assertEquals("p1", currentProducts[0].id)
    }

    @Test
    fun `getProducts should handle unsuccessful response`() {
        // Given
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ProductListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getProducts("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockProductRepository).getProducts("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error obtaining products: 404", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getProducts should handle null response body`() {
        // Given
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(null)
        `when`(mockResponse.code()).thenReturn(200)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ProductListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getProducts("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockProductRepository).getProducts("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Error obtaining products: 200", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getProducts should handle network failure`() {
        // Given
        val throwable = RuntimeException("Network error")
        
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        
        var successResult = false
        var messageResult = ""
        var responseResult: ProductListResponse? = null

        // When
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onFailure(mockCall, throwable)
            null
        }.`when`(mockCall).enqueue(any())

        viewModel.getProducts("") { success, message, response ->
            successResult = success
            messageResult = message
            responseResult = response
        }

        // Then
        verify(mockProductRepository).getProducts("")
        verify(mockCall).enqueue(any())
        assertFalse("Should return failure", successResult)
        assertEquals("Connection error: Network error", messageResult)
        assertNull(responseResult)
    }

    @Test
    fun `getProducts should handle multiple calls`() {
        // Given
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When - Call the same method multiple times
        viewModel.getProducts("") { _, _, _ -> }
        viewModel.getProducts("") { _, _, _ -> }
        viewModel.getProducts("") { _, _, _ -> }

        // Then
        verify(mockProductRepository, times(3)).getProducts("")
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `ProductsViewModel should handle multiple instances`() {
        // Given
        val viewModel1 = ProductsViewModel(mockProductRepository)
        val viewModel2 = ProductsViewModel(mockProductRepository)
        
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        viewModel1.getProducts("") { _, _, _ -> }
        viewModel2.getProducts("") { _, _, _ -> }

        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
        verify(mockProductRepository, times(2)).getProducts("")
    }

    @Test
    fun `getProducts should handle concurrent calls`() {
        // Given
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(500)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When - Execute methods concurrently
        val thread1 = Thread {
            viewModel.getProducts("") { _, _, _ -> }
        }
        val thread2 = Thread {
            viewModel.getProducts("") { _, _, _ -> }
        }
        val thread3 = Thread {
            viewModel.getProducts("") { _, _, _ -> }
        }

        thread1.start()
        thread2.start()
        thread3.start()

        thread1.join()
        thread2.join()
        thread3.join()

        // Then - Method should exist and be callable
        verify(mockProductRepository, times(3)).getProducts("")
        verify(mockCall, times(3)).enqueue(any())
    }

    @Test
    fun `getCurrentProducts should be callable multiple times`() {
        // Given & When
        val call1 = viewModel.getCurrentProducts()
        val call2 = viewModel.getCurrentProducts()
        val call3 = viewModel.getCurrentProducts()

        // Then
        assertNotNull("call1 should not be null", call1)
        assertNotNull("call2 should not be null", call2)
        assertNotNull("call3 should not be null", call3)
    }

    @Test
    fun `getCurrentProducts should return updated products after successful getProducts call`() {
        // Given
        val now = java.util.Date()
        val mockProduct = Product(
            id = "p1",
            name = "Test Product",
            details = "Test Details",
            store = "Test Store",
            batch = "L1",
            image_url = "test-url",
            due_date = "2026-09-24",
            stock = 5,
            price_per_unit = 10.0,
            created_at = "2025-10-23T05:44:07.144451Z"
        )
        val mockProductListResponse = ProductListResponse(products = listOf(mockProduct))
        
        `when`(mockProductRepository.getProducts("")).thenReturn(mockCall)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockProductListResponse)
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<ProductListResponse>>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val initialProducts = viewModel.getCurrentProducts()
        viewModel.getProducts("") { _, _, _ -> }
        val updatedProducts = viewModel.getCurrentProducts()

        // Then
        assertTrue("Initial products should be empty", initialProducts.isEmpty())
        assertEquals("Updated products should have 1 item", 1, updatedProducts.size)
        assertEquals("Product ID should match", "p1", updatedProducts[0].id)
    }
}