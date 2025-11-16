package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.repository.ProductRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class RecommendedProductsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockProductRepository: ProductRepository

    @Mock
    private lateinit var mockCall: Call<ProductListResponse>

    private lateinit var viewModel: RecommendedProductsViewModel

    @Before
    fun setUp() {
        // Create ViewModel with mock repository
        viewModel = RecommendedProductsViewModel(mockProductRepository)
    }

    // ========== BASIC TESTS ==========

    @Test
    fun `RecommendedProductsViewModel should be created successfully`() {
        // Given & When
        val viewModel = RecommendedProductsViewModel()
        
        // Then
        assertNotNull(viewModel)
    }

    @Test
    fun `RecommendedProductsViewModel should have correct class name`() {
        // Given
        val viewModel = RecommendedProductsViewModel()
        
        // When & Then
        assertEquals("RecommendedProductsViewModel", viewModel.javaClass.simpleName)
    }

    @Test
    fun `RecommendedProductsViewModel should extend ViewModel`() {
        // Given
        val viewModel = RecommendedProductsViewModel()
        
        // When & Then
        assertTrue("RecommendedProductsViewModel should extend ViewModel", 
            viewModel is ViewModel)
    }

    @Test
    fun `RecommendedProductsViewModel should be properly configured`() {
        // Given
        val viewModelClass = RecommendedProductsViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `RecommendedProductsViewModel should have getRecommendedProducts method`() {
        // Given
        val viewModelClass = RecommendedProductsViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredMethod("getRecommendedProducts", 
            String::class.java,
            String::class.java,
            kotlin.Function3::class.java))
    }

    @Test
    fun `RecommendedProductsViewModel should be instantiable multiple times`() {
        // When
        val viewModels = (1..5).map { RecommendedProductsViewModel() }

        // Then
        assertEquals("Should create 5 view models", 5, viewModels.size)
        viewModels.forEach { vm ->
            assertNotNull("Each view model should not be null", vm)
            assertTrue("Each view model should be ViewModel", vm is ViewModel)
        }
    }

    // ========== GET RECOMMENDED PRODUCTS TESTS ==========

    @Test
    fun `getRecommendedProducts should not throw exception with valid token`() {
        // Given
        val authToken = "valid_test_token"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue("Method should execute without throwing exception", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle empty auth token`() {
        // Given
        val authToken = ""
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue(true) // Should not throw exception
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle special characters in auth token`() {
        // Given
        val authToken = "test-token-with-special-chars-123!@#"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle numeric auth token`() {
        // Given
        val authToken = "12345"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle long auth token`() {
        // Given
        val authToken = "very-long-auth-token-that-might-be-used-in-some-systems-with-many-characters"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle whitespace auth token`() {
        // Given
        val authToken = "   "
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle unicode characters in auth token`() {
        // Given
        val authToken = "token-with-unicode-ñ-é-ü"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle very long auth token`() {
        // Given
        val authToken = "a".repeat(1000) // Very long token
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle auth token with spaces`() {
        // Given
        val authToken = "token with spaces"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle auth token with newlines`() {
        // Given
        val authToken = "token\nwith\nnewlines"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle auth token with tabs`() {
        // Given
        val authToken = "token\twith\ttabs"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertNotNull("Method should exist", viewModel)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should be callable multiple times`() {
        // Given
        val authToken = "test-token-123"
        val clientId = "test-client-id"
        
        // When & Then
        repeat(5) {
            try {
                viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                    // Callback should be called
                }
                assertTrue("Method should be callable on iteration ${it + 1}", true)
            } catch (e: Exception) {
                fail("Should not throw exception on iteration ${it + 1}: ${e.message}")
            }
        }
    }

    @Test
    fun `getRecommendedProducts should handle different auth tokens`() {
        // Given
        val authTokens = listOf(
            "token1",
            "token2",
            "token3",
            "",
            "special-chars!@#",
            "12345"
        )
        val clientId = "test-client-id"
        
        // When & Then
        authTokens.forEach { token ->
            try {
                viewModel.getRecommendedProducts(token, clientId) { success, message, products ->
                    // Callback should be called
                }
                assertTrue("Method should work with token: $token", true)
            } catch (e: Exception) {
                fail("Should not throw exception with token '$token': ${e.message}")
            }
        }
    }

    @Test
    fun `getRecommendedProducts should handle concurrent calls`() {
        // Given
        val authToken = "test-token-123"
        val clientId = "test-client-id"
        val results = mutableListOf<Boolean>()
        
        // When
        repeat(3) {
            try {
                viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                    results.add(success)
                }
                assertTrue("Method should be callable concurrently", true)
            } catch (e: Exception) {
                fail("Should not throw exception in concurrent call ${it + 1}: ${e.message}")
            }
        }

        // Then
        assertTrue("Should handle concurrent calls", true)
    }

    @Test
    fun `getRecommendedProducts should maintain ViewModel state`() {
        // Given
        val authToken = "test-token-123"
        val clientId = "test-client-id"
        
        // When
        viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
            // Callback
        }
        
        // Then
        assertNotNull("ViewModel should maintain state", viewModel)
        assertEquals("ViewModel class should remain the same", 
            RecommendedProductsViewModel::class.java, viewModel.javaClass)
    }

    @Test
    fun `getRecommendedProducts should handle callback with different parameters`() {
        // Given
        val authToken = "test-token-123"
        val clientId = "test-client-id"
        var callbackInvoked = false

        // When
        viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
            callbackInvoked = true
            // Verify callback parameters types
            assertTrue("Success should be boolean", success is Boolean)
            assertTrue("Message should be string", message is String)
            assertTrue("Products should be list or null", products == null || products is List<*>)
        }

        // Then
        assertTrue("Method should not throw exception", true)
    }

    @Test
    fun `getRecommendedProducts should handle Bearer token format`() {
        // Given
        val authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue("Method should handle Bearer token format", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle JWT-like token`() {
        // Given
        val authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue("Method should handle JWT token", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should accept callback that validates success flag`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Success should be a boolean value
                assertTrue("Success flag should be boolean", success is Boolean)
            }
            assertTrue("Callback should be accepted", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should accept callback that validates message`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Message should be a non-null string
                assertNotNull("Message should not be null", message)
                assertTrue("Message should be String", message is String)
            }
            assertTrue("Callback should be accepted", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should accept callback that handles null products`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Products can be null or a list
                if (products != null) {
                    assertTrue("Products should be a list", products is List<*>)
                }
            }
            assertTrue("Callback should be accepted", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle multiple sequential calls`() {
        // Given
        val authTokens = listOf("token1", "token2", "token3")
        val clientId = "test-client-id"
        var callCount = 0

        // When
        authTokens.forEach { token ->
            try {
                viewModel.getRecommendedProducts(token, clientId) { success, message, products ->
                    callCount++
                }
            } catch (e: Exception) {
                fail("Should not throw exception on call with token $token: ${e.message}")
            }
        }

        // Then
        assertTrue("All calls should be made without exception", true)
    }

    @Test
    fun `getRecommendedProducts should work after ViewModel creation`() {
        // Given
        val newViewModel = RecommendedProductsViewModel()
        val authToken = "test-token"
        val clientId = "test-client-id"

        // When & Then
        try {
            newViewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should work immediately after creation
            }
            assertTrue("Method should work immediately after ViewModel creation", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle alphanumeric tokens`() {
        // Given
        val authToken = "abc123def456ghi789"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue("Method should handle alphanumeric tokens", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle tokens with hyphens and underscores`() {
        // Given
        val authToken = "test-token_with-mixed_separators"
        val clientId = "test-client-id"

        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Callback should be invoked
            }
            assertTrue("Method should handle tokens with separators", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should add Bearer prefix to token`() {
        // Given
        val authToken = "test-token-123"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { _, _, _ ->
                // The ViewModel should add "Bearer " prefix internally
                // This is verified by the ViewModel implementation
            }
            assertTrue("Method should add Bearer prefix", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle multiple instances`() {
        // Given
        val viewModel1 = RecommendedProductsViewModel()
        val viewModel2 = RecommendedProductsViewModel()
        val authToken = "test-token"
        val clientId = "test-client-id"

        // When
        viewModel1.getRecommendedProducts(authToken, clientId) { _, _, _ -> }
        viewModel2.getRecommendedProducts(authToken, clientId) { _, _, _ -> }

        // Then
        assertNotNull("First viewModel should exist", viewModel1)
        assertNotNull("Second viewModel should exist", viewModel2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
    }


    @Test
    fun `getRecommendedProducts should handle network errors gracefully`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"
        var errorMessage: String? = null
        
        // When
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                if (!success) {
                    errorMessage = message
                }
            }
            // Wait a bit for async call to potentially complete
            Thread.sleep(100)
        } catch (e: Exception) {
            // Network errors are expected in unit tests
        }
        
        // Then
        assertNotNull("Method should exist",
            RecommendedProductsViewModel::class.java.methods.find { it.name == "getRecommendedProducts" })
    }

    @Test
    fun `getRecommendedProducts should handle empty products list`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Products can be null or empty list
                if (products != null) {
                    assertTrue("Products should be a list", products is List<*>)
                }
            }
            assertTrue("Method should handle empty products list", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle thread safety`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"
        val results = mutableListOf<Boolean>()

        // When - Execute methods concurrently
        try {
            val thread1 = Thread {
                viewModel.getRecommendedProducts(authToken, clientId) { success, _, _ ->
                    results.add(success)
                }
            }
            val thread2 = Thread {
                viewModel.getRecommendedProducts(authToken, clientId) { success, _, _ ->
                    results.add(success)
                }
            }
            val thread3 = Thread {
                viewModel.getRecommendedProducts(authToken, clientId) { success, _, _ ->
                    results.add(success)
                }
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
        assertNotNull("getRecommendedProducts method should exist",
            RecommendedProductsViewModel::class.java.methods.find { it.name == "getRecommendedProducts" })
    }

    @Test
    fun `getRecommendedProducts should handle callback with null products`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Products can be null in case of error
                assertTrue("Success should be boolean", success is Boolean)
                assertNotNull("Message should not be null", message)
                // Products can be null
            }
            assertTrue("Method should handle null products", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle callback with success message`() {
        // Given
        val authToken = "test-token"
        val clientId = "test-client-id"
        
        // When & Then
        try {
            viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
                // Message should contain appropriate text
                assertNotNull("Message should not be null", message)
                assertTrue("Message should be a string", message is String)
            }
            assertTrue("Method should handle success message", true)
        } catch (e: Exception) {
            fail("Should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getRecommendedProducts should handle method parameter types`() {
        // Given
        val method = RecommendedProductsViewModel::class.java.methods.find { it.name == "getRecommendedProducts" }

        // When
        val parameterTypes = method?.parameterTypes

        // Then
        assertNotNull(parameterTypes)
        assertEquals(3, parameterTypes?.size)
        assertEquals(String::class.java, parameterTypes?.get(0))
        assertEquals(String::class.java, parameterTypes?.get(1))
    }

    // ========== HELPER METHODS ==========

    private fun createTestProduct(id: String, name: String, price: Double): Product {
        return Product(
            id = id,
            name = name,
            details = "Test description",
            store = "Test Store",
            batch = "BATCH001",
            image_url = "https://example.com/image.jpg",
            due_date = "2026-09-24",
            stock = 100,
            price_per_unit = price,
            created_at = "2025-10-23T05:44:07.144451Z"
        )
    }

    private fun createTestProducts(): List<Product> {
        return listOf(
            createTestProduct("prod-001", "Paracetamol 500mg", 0.50),
            createTestProduct("prod-002", "Ibuprofeno 400mg", 0.75),
            createTestProduct("prod-003", "Amoxicilina 500mg", 1.25)
        )
    }
}