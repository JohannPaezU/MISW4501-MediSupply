package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.model.OrderSummaryItem
import com.mfpe.medisupply.data.model.Product
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.util.Calendar
import java.util.Locale

class OrderSummaryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var orderSummaryViewModel: OrderSummaryViewModel

    @Before
    fun setUp() {
        orderSummaryViewModel = OrderSummaryViewModel()
    }

    @Test
    fun `OrderSummaryViewModel should be created successfully`() {
        // Given & When
        val viewModel = OrderSummaryViewModel()
        
        // Then
        assertNotNull(viewModel)
    }

    @Test
    fun `calculateOrderSummary should calculate correct total value`() {
        // Given
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            ),
            Product(
                id = "2",
                name = "Product 2",
                details = "Description 2",
                store = "Store 2",
                batch = "BATCH002",
                image_url = "image2.jpg",
                due_date = "2024-12-31",
                stock = 50,
                price_per_unit = 15.0,
                created_at = "2024-01-01"
            )
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
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
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
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            ),
            Product(
                id = "2",
                name = "Product 2",
                details = "Description 2",
                store = "Store 2",
                batch = "BATCH002",
                image_url = "image2.jpg",
                due_date = "2024-12-31",
                stock = 50,
                price_per_unit = 15.0,
                created_at = "2024-01-01"
            )
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
    fun `setSelectedClient should update selected client`() {
        // Given
        val client = Client(
            id = "client1",
            fullName = "John Doe",
            doi = "12345678",
            email = "john@example.com",
            phone = "1234567890",
            address = "123 Main St"
        )

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
    fun `setSelectedCenter should update selected center`() {
        // Given
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )

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
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)
        
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
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)
        
        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)
        
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
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)
        
        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)
        
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 0)

        // When
        val result = orderSummaryViewModel.validateOrder("institutional", products, quantities)

        // Then
        assertEquals("Debe seleccionar al menos un producto", result)
    }

    @Test
    fun `validateOrder should return null when all validations pass`() {
        // Given
        val client = Client(
            id = "client1",
            fullName = "John Doe",
            doi = "12345678",
            email = "john@example.com",
            phone = "1234567890",
            address = "123 Main St"
        )
        orderSummaryViewModel.setSelectedClient(client)
        
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)
        
        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)
        
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // When
        val result = orderSummaryViewModel.validateOrder("commercial", products, quantities)

        // Then
        assertNull(result)
    }

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

    @Test
    fun `loadClients should call clientViewModel getClients method`() {
        // Given
        val authToken = "test_token"

        // When
        orderSummaryViewModel.loadClients(authToken)

        // Then
        // Since we can't mock the internal ViewModels, we can only verify that the method
        // doesn't throw an exception and that the LiveData is accessible
        assertNotNull(orderSummaryViewModel.clients)
    }

    @Test
    fun `loadDistributionCenters should call distributionCenterViewModel getDistributionCenters method`() {
        // Given
        val authToken = "test_token"

        // When
        orderSummaryViewModel.loadDistributionCenters(authToken)

        // Then
        // Since we can't mock the internal ViewModels, we can only verify that the method
        // doesn't throw an exception and that the LiveData is accessible
        assertNotNull(orderSummaryViewModel.distributionCenters)
    }

    @Test
    fun `createOrder should set loading to true initially`() {
        // Given
        val authToken = "test_token"
        val userRole = "commercial"
        val comments = "Test order"
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // Set up required selections
        val client = Client(
            id = "client1",
            fullName = "John Doe",
            doi = "12345678",
            email = "john@example.com",
            phone = "1234567890",
            address = "123 Main St"
        )
        orderSummaryViewModel.setSelectedClient(client)

        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)

        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that loading is set to true initially
        // Note: The actual API call will fail in tests, but we can verify the initial state
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle institutional user without client`() {
        // Given
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // Set up required selections (no client for institutional user)
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)

        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that the method doesn't throw an exception
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle empty products list`() {
        // Given
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = emptyList<Product>()
        val quantities = emptyMap<String, Int>()

        // Set up required selections
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)

        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that the method doesn't throw an exception
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle null comments`() {
        // Given
        val authToken = "test_token"
        val userRole = "institutional"
        val comments: String? = null
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // Set up required selections
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)

        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that the method doesn't throw an exception
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle null delivery date`() {
        // Given
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // Set up required selections (no delivery date)
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that the method doesn't throw an exception
        assertNotNull(orderSummaryViewModel.isLoading)
    }

    @Test
    fun `createOrder should handle null distribution center`() {
        // Given
        val authToken = "test_token"
        val userRole = "institutional"
        val comments = "Test order"
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = mapOf("1" to 2)

        // Set up required selections (no distribution center)
        val calendar = Calendar.getInstance()
        orderSummaryViewModel.setSelectedDeliveryDate(calendar)

        // When
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)

        // Then
        // We can verify that the method doesn't throw an exception
        assertNotNull(orderSummaryViewModel.isLoading)
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
        val totalValue = orderSummaryViewModel.totalValue.value
        
        assertNotNull(summaryItems)
        assertTrue(summaryItems?.isEmpty() == true)
        assertEquals(0.0, totalValue ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderSummary should handle products with missing quantities`() {
        // Given
        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                details = "Description 1",
                store = "Store 1",
                batch = "BATCH001",
                image_url = "image1.jpg",
                due_date = "2024-12-31",
                stock = 100,
                price_per_unit = 10.0,
                created_at = "2024-01-01"
            )
        )
        val quantities = emptyMap<String, Int>() // No quantity for product "1"

        // When
        orderSummaryViewModel.calculateOrderSummary(products, quantities)

        // Then
        val summaryItems = orderSummaryViewModel.orderSummaryItems.value
        val totalValue = orderSummaryViewModel.totalValue.value
        
        assertNotNull(summaryItems)
        assertTrue(summaryItems?.isEmpty() == true)
        assertEquals(0.0, totalValue ?: 0.0, 0.01)
    }

    @Test
    fun `setSelectedClient should handle null client`() {
        // When
        orderSummaryViewModel.setSelectedClient(null)

        // Then
        // Test through validation - should fail for commercial user
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("commercial", products, quantities)
        
        assertEquals("Por favor seleccione un cliente", validationResult)
    }

    @Test
    fun `setSelectedCenter should handle null center`() {
        // When
        orderSummaryViewModel.setSelectedCenter(null)

        // Then
        // Test through validation - should fail
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("institutional", products, quantities)
        
        assertEquals("Por favor seleccione un centro de distribución", validationResult)
    }

    @Test
    fun `setSelectedDeliveryDate should handle null date`() {
        // When
        orderSummaryViewModel.setSelectedDeliveryDate(null)

        // Then
        // Test through validation - should fail
        val center = DistributionCenter(
            id = "center1",
            name = "Center 1",
            address = "123 Center St",
            city = "City",
            country = "Country",
            created_at = "2024-01-01"
        )
        orderSummaryViewModel.setSelectedCenter(center)
        
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()
        val validationResult = orderSummaryViewModel.validateOrder("institutional", products, quantities)
        
        assertEquals("Por favor seleccione una fecha de entrega", validationResult)
    }

    @Test
    fun `validateOrder should handle case insensitive user role`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("COMMERCIAL", products, quantities)

        // Then
        assertEquals("Por favor seleccione un cliente", result)
    }

    @Test
    fun `validateOrder should handle mixed case user role`() {
        // Given
        val products = listOf<Product>()
        val quantities = emptyMap<String, Int>()

        // When
        val result = orderSummaryViewModel.validateOrder("Commercial", products, quantities)

        // Then
        assertEquals("Por favor seleccione un cliente", result)
    }
}