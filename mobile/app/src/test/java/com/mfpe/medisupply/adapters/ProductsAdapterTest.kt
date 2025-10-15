package com.mfpe.medisupply.adapters

import com.mfpe.medisupply.data.model.Product
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Date

@RunWith(JUnit4::class)
class ProductsAdapterTest {

    @Test
    fun `ProductsAdapter class should exist`() {
        // Given & When
        val adapterClass = ProductsAdapter::class.java

        // Then
        assertNotNull(adapterClass)
        assertEquals("ProductsAdapter", adapterClass.simpleName)
    }

    @Test
    fun `ProductsAdapter should have ProductoViewHolder inner class`() {
        // Given
        val innerClasses = ProductsAdapter::class.java.declaredClasses

        // When
        val viewHolderClass = innerClasses.find { it.simpleName == "ProductoViewHolder" }

        // Then
        assertNotNull(viewHolderClass)
    }

    @Test
    fun `ProductsAdapter should have ProductoDiffCallback inner class`() {
        // Given
        val innerClasses = ProductsAdapter::class.java.declaredClasses

        // When
        val diffCallbackClass = innerClasses.find { it.simpleName == "ProductoDiffCallback" }

        // Then
        assertNotNull(diffCallbackClass)
    }

    @Test
    fun `ProductoDiffCallback areItemsTheSame should compare by id`() {
        // Given
        val callback = ProductsAdapter.ProductoDiffCallback()
        val product1 = createTestProduct("1", "Product 1")
        val product2 = product1.copy(name = "Different Name")
        val product3 = createTestProduct("2", "Product 2")

        // When & Then
        assertTrue(callback.areItemsTheSame(product1, product2))
        assertFalse(callback.areItemsTheSame(product1, product3))
    }

    @Test
    fun `ProductoDiffCallback areContentsTheSame should compare all fields`() {
        // Given
        val callback = ProductsAdapter.ProductoDiffCallback()
        val product1 = createTestProduct("1", "Product 1")
        val product2 = createTestProduct("1", "Product 1")
        val product3 = product1.copy(name = "Different Name")

        // When & Then
        assertTrue(callback.areContentsTheSame(product1, product2))
        assertFalse(callback.areContentsTheSame(product1, product3))
    }

    @Test
    fun `Product model should be properly structured`() {
        // Given & When
        val product = createTestProduct("123", "Test Product")

        // Then
        assertEquals("123", product.id)
        assertEquals("Test Product", product.name)
        assertEquals(10000.0, product.pricePerUnite, 0.01)
        assertEquals(100, product.stock)
    }

    private fun createTestProduct(id: String, name: String): Product {
        return Product(
            id = id,
            name = name,
            details = "Details",
            store = "Store",
            lote = "L001",
            imageUrl = "https://example.com/image.jpg",
            dueDate = Date(),
            stock = 100,
            pricePerUnite = 10000.0,
            providerId = 1,
            providerName = "Provider",
            createdAt = Date()
        )
    }
}
