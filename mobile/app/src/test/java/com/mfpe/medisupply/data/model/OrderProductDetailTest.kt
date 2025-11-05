package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test
import java.io.Serializable

/**
 * Tests unitarios para el modelo OrderProductDetail
 */
class OrderProductDetailTest {

    @Test
    fun `OrderProductDetail should have correct properties`() {
        // Given
        val id = "58829c8f-9384-4763-a82e-effc0201b7ec"
        val name = "Amoxicilina 500mg"
        val store = "CEDI Medellín"
        val batch = "AMX-MED-2025"
        val dueDate = "2026-01-17"
        val pricePerUnit = 18.0
        val quantity = 1

        // When
        val product = OrderProductDetail(
            id = id,
            name = name,
            store = store,
            batch = batch,
            due_date = dueDate,
            price_per_unit = pricePerUnit,
            quantity = quantity
        )

        // Then
        assertEquals("ID should match", id, product.id)
        assertEquals("Name should match", name, product.name)
        assertEquals("Store should match", store, product.store)
        assertEquals("Batch should match", batch, product.batch)
        assertEquals("Due date should match", dueDate, product.due_date)
        assertEquals("Price per unit should match", pricePerUnit, product.price_per_unit, 0.01)
        assertEquals("Quantity should match", quantity, product.quantity)
    }

    @Test
    fun `OrderProductDetail should be Serializable`() {
        // Given
        val product = createTestOrderProductDetail()

        // When & Then
        assertTrue("OrderProductDetail should implement Serializable", product is Serializable)
    }

    @Test
    fun `OrderProductDetail should support equals and hashCode`() {
        // Given
        val product1 = OrderProductDetail(
            id = "product-123",
            name = "Product 1",
            store = "Store 1",
            batch = "BATCH-001",
            due_date = "2025-12-31",
            price_per_unit = 10.5,
            quantity = 2
        )
        val product2 = OrderProductDetail(
            id = "product-123",
            name = "Product 1",
            store = "Store 1",
            batch = "BATCH-001",
            due_date = "2025-12-31",
            price_per_unit = 10.5,
            quantity = 2
        )
        val product3 = OrderProductDetail(
            id = "product-456",
            name = "Product 2",
            store = "Store 2",
            batch = "BATCH-002",
            due_date = "2026-01-01",
            price_per_unit = 20.0,
            quantity = 1
        )

        // When & Then
        assertEquals("Equal products should be equal", product1, product2)
        assertEquals("Equal products should have same hashCode", product1.hashCode(), product2.hashCode())
        assertNotEquals("Different products should not be equal", product1, product3)
        assertNotEquals("Different products should have different hashCode", product1.hashCode(), product3.hashCode())
    }

    @Test
    fun `OrderProductDetail should support toString`() {
        // Given
        val product = createTestOrderProductDetail()

        // When
        val toString = product.toString()

        // Then
        assertNotNull("toString should not be null", toString)
        assertTrue("toString should contain class name", toString.contains("OrderProductDetail"))
        assertTrue("toString should contain id", toString.contains(product.id))
        assertTrue("toString should contain name", toString.contains(product.name))
    }

    @Test
    fun `OrderProductDetail should handle empty strings`() {
        // Given
        val product = OrderProductDetail(
            id = "",
            name = "",
            store = "",
            batch = "",
            due_date = "",
            price_per_unit = 0.0,
            quantity = 0
        )

        // When & Then
        assertTrue("ID should be empty", product.id.isEmpty())
        assertTrue("Name should be empty", product.name.isEmpty())
        assertTrue("Store should be empty", product.store.isEmpty())
        assertTrue("Batch should be empty", product.batch.isEmpty())
        assertTrue("Due date should be empty", product.due_date.isEmpty())
        assertEquals("Price per unit should be 0", 0.0, product.price_per_unit, 0.01)
        assertEquals("Quantity should be 0", 0, product.quantity)
    }

    @Test
    fun `OrderProductDetail should handle special characters`() {
        // Given
        val product = OrderProductDetail(
            id = "product-123!@#",
            name = "Producto Ñoño",
            store = "CEDI #45-67",
            batch = "BATCH-2025!@#",
            due_date = "2025-12-31",
            price_per_unit = 15.99,
            quantity = 5
        )

        // When & Then
        assertEquals("ID should contain special characters", "product-123!@#", product.id)
        assertEquals("Name should contain special characters", "Producto Ñoño", product.name)
        assertEquals("Store should contain special characters", "CEDI #45-67", product.store)
        assertEquals("Batch should contain special characters", "BATCH-2025!@#", product.batch)
    }

    @Test
    fun `OrderProductDetail should handle different price values`() {
        // Given
        val testCases = listOf(
            Pair(0.0, 1),
            Pair(0.01, 1),
            Pair(999.99, 1),
            Pair(1000.0, 1),
            Pair(18.5, 10)
        )

        // When & Then
        testCases.forEach { (price, quantity) ->
            val product = OrderProductDetail(
                id = "product-${price}",
                name = "Product",
                store = "Store",
                batch = "BATCH",
                due_date = "2025-12-31",
                price_per_unit = price,
                quantity = quantity
            )
            assertEquals("Price should match", price, product.price_per_unit, 0.01)
            assertEquals("Quantity should match", quantity, product.quantity)
        }
    }

    @Test
    fun `OrderProductDetail should handle different quantity values`() {
        // Given
        val quantities = listOf(0, 1, 10, 100, 1000, Int.MAX_VALUE)

        // When & Then
        quantities.forEach { quantity ->
            val product = OrderProductDetail(
                id = "product-${quantity}",
                name = "Product",
                store = "Store",
                batch = "BATCH",
                due_date = "2025-12-31",
                price_per_unit = 10.0,
                quantity = quantity
            )
            assertEquals("Quantity should match", quantity, product.quantity)
        }
    }

    @Test
    fun `OrderProductDetail should support copy method`() {
        // Given
        val originalProduct = createTestOrderProductDetail()

        // When
        val copiedProduct = originalProduct.copy(
            name = "Updated Name",
            quantity = 5
        )

        // Then
        assertEquals("ID should remain the same", originalProduct.id, copiedProduct.id)
        assertEquals("Store should remain the same", originalProduct.store, copiedProduct.store)
        assertEquals("Batch should remain the same", originalProduct.batch, copiedProduct.batch)
        assertEquals("Due date should remain the same", originalProduct.due_date, copiedProduct.due_date)
        assertEquals("Price per unit should remain the same", originalProduct.price_per_unit, copiedProduct.price_per_unit, 0.01)
        assertEquals("Name should be updated", "Updated Name", copiedProduct.name)
        assertEquals("Quantity should be updated", 5, copiedProduct.quantity)
    }

    @Test
    fun `OrderProductDetail should support componentN methods`() {
        // Given
        val product = createTestOrderProductDetail()

        // When
        val (id, name, store, batch, due_date, price_per_unit, quantity) = product

        // Then
        assertEquals("Component 1 should be id", product.id, id)
        assertEquals("Component 2 should be name", product.name, name)
        assertEquals("Component 3 should be store", product.store, store)
        assertEquals("Component 4 should be batch", product.batch, batch)
        assertEquals("Component 5 should be due_date", product.due_date, due_date)
        assertEquals("Component 6 should be price_per_unit", product.price_per_unit, price_per_unit, 0.01)
        assertEquals("Component 7 should be quantity", product.quantity, quantity)
    }

    @Test
    fun `OrderProductDetail should handle long strings`() {
        // Given
        val longId = "very-long-product-id-that-might-be-used-in-some-systems-with-many-characters"
        val longName = "Very Long Product Name That Might Be Used In Some Systems With Many Characters"
        val longStore = "Very Long Store Name That Might Be Used In Some Systems"
        val longBatch = "Very Long Batch Number That Might Be Used In Some Systems"

        val product = OrderProductDetail(
            id = longId,
            name = longName,
            store = longStore,
            batch = longBatch,
            due_date = "2025-12-31",
            price_per_unit = 10.0,
            quantity = 1
        )

        // When & Then
        assertEquals("Long ID should be preserved", longId, product.id)
        assertEquals("Long name should be preserved", longName, product.name)
        assertEquals("Long store should be preserved", longStore, product.store)
        assertEquals("Long batch should be preserved", longBatch, product.batch)
    }

    @Test
    fun `OrderProductDetail should handle different date formats`() {
        // Given
        val dateFormats = listOf(
            "2025-12-31",
            "2026-01-01",
            "2027-12-31",
            "2025-01-01"
        )

        // When & Then
        dateFormats.forEach { date ->
            val product = OrderProductDetail(
                id = "product-${date}",
                name = "Product",
                store = "Store",
                batch = "BATCH",
                due_date = date,
                price_per_unit = 10.0,
                quantity = 1
            )
            assertEquals("Date should match", date, product.due_date)
        }
    }

    @Test
    fun `OrderProductDetail should maintain data integrity`() {
        // Given
        val product = createTestOrderProductDetail()

        // When
        val productCopy = product.copy()

        // Then
        assertEquals("Copy should be equal to original", product, productCopy)
        assertEquals("Copy should have same hashCode", product.hashCode(), productCopy.hashCode())
        assertNotSame("Copy should be different instance", product, productCopy)
    }

    // Helper method to create a test order product detail
    private fun createTestOrderProductDetail(): OrderProductDetail {
        return OrderProductDetail(
            id = "58829c8f-9384-4763-a82e-effc0201b7ec",
            name = "Amoxicilina 500mg",
            store = "CEDI Medellín",
            batch = "AMX-MED-2025",
            due_date = "2026-01-17",
            price_per_unit = 18.0,
            quantity = 1
        )
    }
}

