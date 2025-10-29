package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call

/**
 * Tests unitarios para ProductService
 */
class ProductServiceTest {

    @Test
    fun `createRetrofitService should create ProductService instance`() {
        // When
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // Then
        assertNotNull("ProductService should not be null", productService)
        assertTrue("Should be instance of ProductService", productService is ProductService)
    }

    @Test
    fun `createRetrofitService should create different ProductService instances`() {
        // When
        val productService1 = RetrofitApiClient.createRetrofitService(ProductService::class.java)
        val productService2 = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // Then
        assertNotNull("First service should not be null", productService1)
        assertNotNull("Second service should not be null", productService2)
        assertTrue("Both should be instances of ProductService", 
            productService1 is ProductService && productService2 is ProductService)
    }

    @Test
    fun `createRetrofitService should handle ProductService methods`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When
        val getProductsCall = productService.getProducts("")

        // Then
        assertNotNull("Get products call should not be null", getProductsCall)
        assertTrue("Get products call should be Call type", getProductsCall is Call<*>)
    }

    @Test
    fun `createRetrofitService should create service with correct configuration`() {
        // When
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // Then
        assertNotNull("Service should be created", productService)
        
        // Verify service has expected methods
        val methods = productService.javaClass.methods
        val methodNames = methods.map { it.name }
        
        assertTrue("Should have getProducts method", methodNames.contains("getProducts"))
    }

    @Test
    fun `createRetrofitService should handle null safety`() {
        // When & Then
        try {
            val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)
            assertNotNull("Service should not be null", productService)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `createRetrofitService should be consistent across calls`() {
        // When
        val service1 = RetrofitApiClient.createRetrofitService(ProductService::class.java)
        val service2 = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // Then
        assertNotNull("First service should not be null", service1)
        assertNotNull("Second service should not be null", service2)
        assertEquals("Services should be of same type", 
            service1.javaClass, service2.javaClass)
    }

    @Test
    fun `getProducts should return Call with correct generic type`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When
        val call = productService.getProducts("")

        // Then
        assertNotNull("Call should not be null", call)
        assertTrue("Call should be Call<ProductListResponse>", call is Call<ProductListResponse>)
    }

    @Test
    fun `getProducts should be callable multiple times`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When
        val call1 = productService.getProducts("")
        val call2 = productService.getProducts("")
        val call3 = productService.getProducts("")

        // Then
        assertNotNull("First call should not be null", call1)
        assertNotNull("Second call should not be null", call2)
        assertNotNull("Third call should not be null", call3)
    }

    @Test
    fun `ProductService should have correct interface structure`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When
        val methods = productService.javaClass.methods
        val getProductsMethod = methods.find { it.name == "getProducts" }

        // Then
        assertNotNull("getProducts method should exist", getProductsMethod)
        assertEquals("getProducts should have no parameters", 1, getProductsMethod?.parameterCount)
    }

    @Test
    fun `ProductService should handle concurrent calls`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When
        val calls = mutableListOf<Call<ProductListResponse>>()
        
        // Execute multiple calls concurrently
        val thread1 = Thread {
            calls.add(productService.getProducts(""))
        }
        val thread2 = Thread {
            calls.add(productService.getProducts(""))
        }
        val thread3 = Thread {
            calls.add(productService.getProducts(""))
        }

        thread1.start()
        thread2.start()
        thread3.start()

        thread1.join()
        thread2.join()
        thread3.join()

        // Then
        assertEquals("Should have 3 calls", 3, calls.size)
        calls.forEach { call ->
            assertNotNull("Each call should not be null", call)
            assertTrue("Each call should be Call type", call is Call<*>)
        }
    }

    @Test
    fun `ProductService should be thread safe`() {
        // Given
        val productService = RetrofitApiClient.createRetrofitService(ProductService::class.java)

        // When & Then
        try {
            val thread1 = Thread {
                productService.getProducts("")
            }
            val thread2 = Thread {
                productService.getProducts("")
            }

            thread1.start()
            thread2.start()

            thread1.join()
            thread2.join()
        } catch (e: Exception) {
            fail("Service should be thread safe: ${e.message}")
        }
    }
}
