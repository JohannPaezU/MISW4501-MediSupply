package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.model.Product
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.Calendar

class OrderSummaryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var orderSummaryViewModel: OrderSummaryViewModel

    @Before
    fun setUp() {
        orderSummaryViewModel = OrderSummaryViewModel()
    }

    // ========== BASIC TESTS ==========

    @Test
    fun `OrderSummaryViewModel should be created successfully`() {
        // Given & When
        val viewModel = OrderSummaryViewModel()
        
        // Then
        assertNotNull(viewModel)
    }

    @Test
    fun `LiveData should be properly initialized`() {
        // Then
        assertNotNull(orderSummaryViewModel.clients)
        assertNotNull(orderSummaryViewModel.distributionCenters)
        assertNotNull(orderSummaryViewModel.orderSummaryItems)
        assertNotNull(orderSummaryViewModel.totalValue)
        assertNotNull(orderSummaryViewModel.isLoading)
        assertNotNull(orderSummaryViewModel.errorMessage)
        assertNotNull(orderSummaryViewModel.orderCreated)
    }

    @Test
    fun `LiveData should have correct initial values`() {
        // Then
        assertNull(orderSummaryViewModel.clients.value)
        assertNull(orderSummaryViewModel.distributionCenters.value)
        assertNull(orderSummaryViewModel.orderSummaryItems.value)
        assertNull(orderSummaryViewModel.totalValue.value)
        assertNull(orderSummaryViewModel.isLoading.value)
        assertNull(orderSummaryViewModel.errorMessage.value)
        assertNull(orderSummaryViewModel.orderCreated.value)
    }

    // ========== CALCULATE ORDER SUMMARY TESTS ==========

    @Test
    fun `calculateOrderSummary should calculate correct total value`() {
        // Given
        val products = listOf(
            createTestProduct("1", "Product 1", 10.0),
            createTestProduct("2", "Product 2", 15.0)
        )
        val quantities = mapOf("1" to 2, "2" to 3)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val totalValue = orderSummaryViewModel.totalValue.value
        assertEquals(65.0, totalValue ?: 0.0, 0.01) // (10*2) + (15*3) = 65
    }

    @Test
    fun `calculateOrderSummary should create correct order summary items`() {
        // Given
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        assertNotNull(summaryItems)
        assertEquals(1, summaryItems?.size)
        
        val item = summaryItems?.first()
        assertEquals("1", item?.id)
        assertEquals("Product 1", item?.name)
        assertEquals(20.0, item?.price ?: 0.0, 0.01) // 10 * 2
        assertEquals(2, item?.quantity)
    }

    @Test
    fun `calculateOrderSummary should ignore products with zero quantity`() {
        // Given
        val products = listOf(
            createTestProduct("1", "Product 1", 10.0),
            createTestProduct("2", "Product 2", 15.0)
        )
        val quantities = mapOf("1" to 2, "2" to 0)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        assertEquals(1, summaryItems?.size)
        assertEquals(20.0, orderSummaryViewModel.totalValue.value ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should ignore products with negative quantity`() {
        // Given
        val products = listOf(
            createTestProduct("1", "Product 1", 10.0),
            createTestProduct("2", "Product 2", 15.0)
        )
        val quantities = mapOf("1" to 2, "2" to -1)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        assertEquals(1, summaryItems?.size)
        assertEquals(20.0, orderSummaryViewModel.totalValue.value ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle empty products list`() {
        // Given
        val products = emptyList<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        assertEquals(0, summaryItems?.size)
        assertEquals(0.0, orderSummaryViewModel.totalValue.value ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle products with no quantity mapping`() {
        // Given
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = emptyMap<String, Int>()

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        assertEquals(0, summaryItems?.size)
        assertEquals(0.0, orderSummaryViewModel.totalValue.value ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle large quantities`() {
        // Given
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 1000)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val totalValue = orderSummaryViewModel.totalValue.value
        assertEquals(10000.0, totalValue ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle decimal prices`() {
        // Given
        val products = listOf(createTestProduct("1", "Product 1", 10.99))
        val quantities = mapOf("1" to 3)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val totalValue = orderSummaryViewModel.totalValue.value
        assertEquals(32.97, totalValue ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle multiple products with different quantities`() {
        // Given
        val products = listOf(
            createTestProduct("1", "Product 1", 5.50),
            createTestProduct("2", "Product 2", 12.75),
            createTestProduct("3", "Product 3", 8.00)
        )
        val quantities = mapOf("1" to 5, "2" to 2, "3" to 10)

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val totalValue = orderSummaryViewModel.totalValue.value
        assertEquals(133.0, totalValue ?: 0.0, 0.01) // (5.5*5) + (12.75*2) + (8*10)
    }

    // ========== SETTER TESTS ==========

    @Test
    fun `setSelectedClient should update selected client`() {
        // Given
        val client = createTestClient()

        // When
        orderSummaryViewModel.setSelectedClient(client)

        // Then
        // We can't directly test the private field, but we can test through validation
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("commercial", products, quantities)
        
        // Should not fail due to missing client since we set one
        assertNotEquals("Por favor seleccione un cliente", validationResult)
    }

    @Test
    fun `setSelectedClient should handle null client`() {
        // When
        orderSummaryViewModel.setSelectedClient(null)

        // Then
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("commercial", products, quantities)

        // Should fail due to missing client
        assertEquals("Por favor seleccione un cliente", validationResult)
    }

    @Test
    fun `setSelectedCenter should update selected center`() {
        // Given
        val center = createTestDistributionCenter()

        // When
        orderSummaryViewModel.setSelectedCenter(center)

        // Then
        // Test through validation
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("commercial", products, quantities)
        
        // Should not fail due to missing center since we set one
        assertNotEquals("Por favor seleccione un centro de distribución", validationResult)
    }

    @Test
    fun `setSelectedCenter should handle null center`() {
        // When
        orderSummaryViewModel.setSelectedCenter(null)

        // Then
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Should fail due to missing center
        assertEquals("Por favor seleccione un centro de distribución", validationResult)
    }

    @Test
    fun `setSelectedDeliveryDate should update selected date`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.DECEMBER, 25)

        // When
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // Then
        // Test through validation
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("commercial", products, quantities)
        
        // Should not fail due to missing date since we set one
        assertNotEquals("Por favor seleccione una fecha de entrega", validationResult)
    }

    @Test
    fun `setSelectedDeliveryDate should handle null date`() {
        // When
        orderSummaryViewModel.setSelectedDeliveryDate(null)

        // Then
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        val validationResult = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Should fail due to missing date
        assertEquals("Por favor seleccione una fecha de entrega", validationResult)
    }

    // ========== VALIDATION TESTS ==========

    @Test
    fun `validateOrder should return error for missing client when user is commercial`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("commercial", products, quantities)

        // Then
        assertEquals("Por favor seleccione un cliente", result)
    }

    @Test
    fun `validateOrder should return error for missing client when user is COMMERCIAL uppercase`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("COMMERCIAL", products, quantities)

        // Then
        assertEquals("Por favor seleccione un cliente", result)
    }

    @Test
    fun `validateOrder should not require client when user is not commercial`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertNotEquals("Por favor seleccione un cliente", result)
    }

    @Test
    fun `validateOrder should return error for missing distribution center`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertEquals("Por favor seleccione un centro de distribución", result)
    }

    @Test
    fun `validateOrder should return error for missing delivery date`() {
        // Given
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertEquals("Por favor seleccione una fecha de entrega", result)
    }

    @Test
    fun `validateOrder should return error for no products selected`() {
        // Given
        setupValidOrder()
        val products = emptyList<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertEquals("Debe seleccionar al menos un producto", result)
    }

    @Test
    fun `validateOrder should return error for products with zero quantities`() {
        // Given
        setupValidOrder()
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 0)

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertEquals("Debe seleccionar al menos un producto", result)
    }

    @Test
    fun `validateOrder should return null when all validations pass for commercial user`() {
        // Given
        orderSummaryViewModel.setSelectedClient(createTestClient())
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        orderSummaryViewModel.setSelectedDeliveryDate(Calendar.getInstance())
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        val result = orderSummaryViewModel.validateOrder("commercial", products, quantities)

        // Then
        assertNull(result)
    }

    @Test
    fun `validateOrder should return null when all validations pass for institutional user`() {
        // Given
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        orderSummaryViewModel.setSelectedDeliveryDate(Calendar.getInstance())
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertNull(result)
    }

    @Test
    fun `validateOrder should handle multiple products with mixed quantities`() {
        // Given
        setupValidOrder()
        val products = listOf(
            createTestProduct("1", "Product 1", 10.0),
            createTestProduct("2", "Product 2", 15.0)
        )
        val quantities = mapOf("1" to 0, "2" to 5)

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertNull(result) // Should pass because at least one product has quantity > 0
    }

    // ========== CLEAR METHODS TESTS ==========

    @Test
    fun `clearError should clear error message`() {
        // When
        orderSummaryViewModel.clearError()

        // Then
        assertNull(orderSummaryViewModel.errorMessage.value)
    }

    @Test
    fun `clearOrderCreated should clear order created flag`() {
        // When
        orderSummaryViewModel.clearOrderCreated()

        // Then
        assertEquals(false, orderSummaryViewModel.orderCreated.value)
    }

    // ========== LOAD METHODS TESTS ==========

    @Test
    fun `loadClients should not throw exception`() {
        // Given
        val authToken = "test_token"

        // When & Then
        try {
            orderSummaryViewModel.loadClients(authToken)
            assertNotNull(orderSummaryViewModel.clients)
        } catch (e: Exception) {
            fail("loadClients should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `loadDistributionCenters should not throw exception`() {
        // Given
        val authToken = "test_token"

        // When & Then
        try {
            orderSummaryViewModel.loadDistributionCenters(authToken)
            assertNotNull(orderSummaryViewModel.distributionCenters)
        } catch (e: Exception) {
            fail("loadDistributionCenters should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `loadClients should handle empty auth token`() {
        // Given
        val authToken = ""

        // When & Then
        try {
            orderSummaryViewModel.loadClients(authToken)
            assertTrue(true)
        } catch (e: Exception) {
            fail("Should handle empty token: ${e.message}")
        }
    }

    @Test
    fun `loadDistributionCenters should handle empty auth token`() {
        // Given
        val authToken = ""

        // When & Then
        try {
            orderSummaryViewModel.loadDistributionCenters(authToken)
            assertTrue(true)
        } catch (e: Exception) {
            fail("Should handle empty token: ${e.message}")
        }
    }

    // ========== CREATE ORDER TESTS ==========

    @Test
    fun `createOrder should not throw exception with valid data`() {
        // Given
        setupValidOrder()
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When & Then
        try {
            orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)
            assertNotNull(orderSummaryViewModel.isLoading)
        } catch (e: Exception) {
            fail("createOrder should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `createOrder should handle institutional user without client`() {
        // Given
        setupValidOrder()
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle commercial user with client`() {
        // Given
        orderSummaryViewModel.setSelectedClient(createTestClient())
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        orderSummaryViewModel.setSelectedDeliveryDate(Calendar.getInstance())
        val authToken = "test_token"
        val userRole = "commercial"
        val comments = "Test order"
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle null comments`() {
        // Given
        setupValidOrder()
        val authToken = "test_token"
        val userRole = "institutional"
        val comments: String? = null
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle empty comments`() {
        // Given
        setupValidOrder()
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = ""
        val products = listOf(createTestProduct("1", "Product 1", 10.0))
        val quantities = mapOf("1" to 2)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle multiple products`() {
        // Given
        setupValidOrder()
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Multiple products order"
        val products = listOf(
            createTestProduct("1", "Product 1", 10.0),
            createTestProduct("2", "Product 2", 15.0),
            createTestProduct("3", "Product 3", 20.0)
        )
        val quantities = mapOf("1" to 2, "2" to 3, "3" to 1)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    // ========== HELPER METHODS ==========

    private fun createTestProduct(id: String, name: String, price: Double): Product {
        return Product(
            id = id,
            name = name,
            details = "Test description",
            store = "Test store",
            batch = "BATCH001",
            image_url = "test.jpg",
            due_date = "2024-12-31",
            stock = 100,
            price_per_unit = price,
            created_at = "2024-01-01"
        )
    }

    private fun createTestClient(): Client {
        return Client(
            id = "client1",
            full_name = "John Doe",
            doi = "12345678",
            email = "john@example.com",
            phone = "1234567890",
            address = "123 Main St",
            role = "institutional",
            created_at = "2024-01-01T00:00:00Z"
        )
    }

    private fun createTestDistributionCenter(): DistributionCenter {
        return DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
    }

    private fun setupValidOrder() {
        orderSummaryViewModel.setSelectedCenter(createTestDistributionCenter())
        orderSummaryViewModel.setSelectedDeliveryDate(Calendar.getInstance())
    }
}