package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test
import java.io.Serializable

/**
 * Tests unitarios para el modelo OrderDetailResponse
 */
class OrderDetailResponseTest {

    @Test
    fun `OrderDetailResponse should have correct properties`() {
        // Given
        val id = "d0a2d69d-b082-42e4-99eb-733e413e2877"
        val comments = "prueba con vendedor"
        val deliveryDate = "2025-11-06"
        val status = "received"
        val createdAt = "2025-11-05T02:29:56.468239Z"
        val client = createTestClient()
        val distributionCenter = createTestDistributionCenter()
        val products = listOf(createTestOrderProductDetail())

        // When
        val orderDetail = OrderDetailResponse(
            id = id,
            comments = comments,
            delivery_date = deliveryDate,
            status = status,
            created_at = createdAt,
            client = client,
            distribution_center = distributionCenter,
            products = products
        )

        // Then
        assertEquals("ID should match", id, orderDetail.id)
        assertEquals("Comments should match", comments, orderDetail.comments)
        assertEquals("Delivery date should match", deliveryDate, orderDetail.delivery_date)
        assertEquals("Status should match", status, orderDetail.status)
        assertEquals("Created at should match", createdAt, orderDetail.created_at)
        assertEquals("Client should match", client, orderDetail.client)
        assertEquals("Distribution center should match", distributionCenter, orderDetail.distribution_center)
        assertEquals("Products should match", products, orderDetail.products)
        assertEquals("Products size should match", 1, orderDetail.products.size)
    }

    @Test
    fun `OrderDetailResponse should handle null comments`() {
        // Given
        val orderDetail = OrderDetailResponse(
            id = "order-123",
            comments = null,
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = createTestClient(),
            distribution_center = createTestDistributionCenter(),
            products = emptyList()
        )

        // When & Then
        assertNull("Comments should be null", orderDetail.comments)
    }

    @Test
    fun `OrderDetailResponse should be Serializable`() {
        // Given
        val orderDetail = createTestOrderDetailResponse()

        // When & Then
        assertTrue("OrderDetailResponse should implement Serializable", orderDetail is Serializable)
    }

    @Test
    fun `OrderDetailResponse should support equals and hashCode`() {
        // Given
        val client = createTestClient()
        val distributionCenter = createTestDistributionCenter()
        val products = listOf(createTestOrderProductDetail())

        val orderDetail1 = OrderDetailResponse(
            id = "order-123",
            comments = "Comment 1",
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = client,
            distribution_center = distributionCenter,
            products = products
        )
        val orderDetail2 = OrderDetailResponse(
            id = "order-123",
            comments = "Comment 1",
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = client,
            distribution_center = distributionCenter,
            products = products
        )
        val orderDetail3 = OrderDetailResponse(
            id = "order-456",
            comments = "Comment 2",
            delivery_date = "2025-12-06",
            status = "delivered",
            created_at = "2025-12-05T02:29:56.468239Z",
            client = client,
            distribution_center = distributionCenter,
            products = products
        )

        // When & Then
        assertEquals("Equal order details should be equal", orderDetail1, orderDetail2)
        assertEquals("Equal order details should have same hashCode", orderDetail1.hashCode(), orderDetail2.hashCode())
        assertNotEquals("Different order details should not be equal", orderDetail1, orderDetail3)
        assertNotEquals("Different order details should have different hashCode", orderDetail1.hashCode(), orderDetail3.hashCode())
    }

    @Test
    fun `OrderDetailResponse should support toString`() {
        // Given
        val orderDetail = createTestOrderDetailResponse()

        // When
        val toString = orderDetail.toString()

        // Then
        assertNotNull("toString should not be null", toString)
        assertTrue("toString should contain class name", toString.contains("OrderDetailResponse"))
        assertTrue("toString should contain id", toString.contains(orderDetail.id))
        assertTrue("toString should contain status", toString.contains(orderDetail.status))
    }

    @Test
    fun `OrderDetailResponse should handle empty products list`() {
        // Given
        val orderDetail = OrderDetailResponse(
            id = "order-123",
            comments = "No products",
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = createTestClient(),
            distribution_center = createTestDistributionCenter(),
            products = emptyList()
        )

        // When & Then
        assertTrue("Products list should be empty", orderDetail.products.isEmpty())
        assertEquals("Products size should be 0", 0, orderDetail.products.size)
    }

    @Test
    fun `OrderDetailResponse should handle multiple products`() {
        // Given
        val products = listOf(
            createTestOrderProductDetail(),
            OrderProductDetail(
                id = "e6aab4f0-9122-4a2f-977e-e80dd54e91db",
                name = "Ibuprofeno 400mg",
                store = "CEDI Bogotá",
                batch = "IBU-BOG-2025",
                due_date = "2027-01-16",
                price_per_unit = 12.5,
                quantity = 1,
                image_url = "https://example.com/image2.jpg"
            ),
            OrderProductDetail(
                id = "68e1e44b-bc60-42bd-b8d0-e1cfa349da2b",
                name = "Loratadina 10mg",
                store = "CEDI Bucaramanga",
                batch = "LRT-BCM-2025",
                due_date = "2026-08-15",
                price_per_unit = 11.2,
                quantity = 1,
                image_url = "https://example.com/image3.jpg"
            )
        )

        val orderDetail = OrderDetailResponse(
            id = "order-123",
            comments = "Multiple products",
            delivery_date = "2025-11-06",
            status = "received",
            created_at = "2025-11-05T02:29:56.468239Z",
            client = createTestClient(),
            distribution_center = createTestDistributionCenter(),
            products = products
        )

        // When & Then
        assertEquals("Products size should be 3", 3, orderDetail.products.size)
        assertEquals("First product should match", products[0], orderDetail.products[0])
        assertEquals("Second product should match", products[1], orderDetail.products[1])
        assertEquals("Third product should match", products[2], orderDetail.products[2])
    }

    @Test
    fun `OrderDetailResponse should handle different status values`() {
        // Given
        val statuses = listOf("received", "in_preparation", "in_transit", "delivered", "returned")

        // When & Then
        statuses.forEach { status ->
            val orderDetail = OrderDetailResponse(
                id = "order-${status}",
                comments = "Test",
                delivery_date = "2025-11-06",
                status = status,
                created_at = "2025-11-05T02:29:56.468239Z",
                client = createTestClient(),
                distribution_center = createTestDistributionCenter(),
                products = emptyList()
            )
            assertEquals("Status should match", status, orderDetail.status)
        }
    }

    @Test
    fun `OrderDetailResponse should support copy method`() {
        // Given
        val originalOrderDetail = createTestOrderDetailResponse()

        // When
        val copiedOrderDetail = originalOrderDetail.copy(
            status = "delivered",
            comments = "Updated comment"
        )

        // Then
        assertEquals("ID should remain the same", originalOrderDetail.id, copiedOrderDetail.id)
        assertEquals("Delivery date should remain the same", originalOrderDetail.delivery_date, copiedOrderDetail.delivery_date)
        assertEquals("Created at should remain the same", originalOrderDetail.created_at, copiedOrderDetail.created_at)
        assertEquals("Client should remain the same", originalOrderDetail.client, copiedOrderDetail.client)
        assertEquals("Distribution center should remain the same", originalOrderDetail.distribution_center, copiedOrderDetail.distribution_center)
        assertEquals("Products should remain the same", originalOrderDetail.products, copiedOrderDetail.products)
        assertEquals("Status should be updated", "delivered", copiedOrderDetail.status)
        assertEquals("Comments should be updated", "Updated comment", copiedOrderDetail.comments)
    }

    @Test
    fun `OrderDetailResponse should support componentN methods`() {
        // Given
        val orderDetail = createTestOrderDetailResponse()

        // When
        val (id, comments, delivery_date, status, created_at, client, distribution_center, products) = orderDetail

        // Then
        assertEquals("Component 1 should be id", orderDetail.id, id)
        assertEquals("Component 2 should be comments", orderDetail.comments, comments)
        assertEquals("Component 3 should be delivery_date", orderDetail.delivery_date, delivery_date)
        assertEquals("Component 4 should be status", orderDetail.status, status)
        assertEquals("Component 5 should be created_at", orderDetail.created_at, created_at)
        assertEquals("Component 6 should be client", orderDetail.client, client)
        assertEquals("Component 7 should be distribution_center", orderDetail.distribution_center, distribution_center)
        assertEquals("Component 8 should be products", orderDetail.products, products)
    }

    @Test
    fun `OrderDetailResponse should handle different date formats`() {
        // Given
        val deliveryDates = listOf(
            "2025-11-06",
            "2026-12-31",
            "2027-01-01"
        )

        // When & Then
        deliveryDates.forEach { deliveryDate ->
            val orderDetail = OrderDetailResponse(
                id = "order-${deliveryDate}",
                comments = "Test",
                delivery_date = deliveryDate,
                status = "received",
                created_at = "2025-11-05T02:29:56.468239Z",
                client = createTestClient(),
                distribution_center = createTestDistributionCenter(),
                products = emptyList()
            )
            assertEquals("Delivery date should match", deliveryDate, orderDetail.delivery_date)
        }
    }

    @Test
    fun `OrderDetailResponse should maintain data integrity`() {
        // Given
        val orderDetail = createTestOrderDetailResponse()

        // When
        val orderDetailCopy = orderDetail.copy()

        // Then
        assertEquals("Copy should be equal to original", orderDetail, orderDetailCopy)
        assertEquals("Copy should have same hashCode", orderDetail.hashCode(), orderDetailCopy.hashCode())
        assertNotSame("Copy should be different instance", orderDetail, orderDetailCopy)
    }

    @Test
    fun `OrderDetailResponse should handle empty strings`() {
        // Given
        val orderDetail = OrderDetailResponse(
            id = "",
            comments = "",
            delivery_date = "",
            status = "",
            created_at = "",
            client = createTestClient(),
            distribution_center = createTestDistributionCenter(),
            products = emptyList()
        )

        // When & Then
        assertTrue("ID should be empty", orderDetail.id.isEmpty())
        assertTrue("Comments should be empty", orderDetail.comments?.isEmpty() ?: false)
        assertTrue("Delivery date should be empty", orderDetail.delivery_date.isEmpty())
        assertTrue("Status should be empty", orderDetail.status.isEmpty())
        assertTrue("Created at should be empty", orderDetail.created_at.isEmpty())
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

