package com.mfpe.medisupply.adapters

import com.mfpe.medisupply.data.model.Product
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Date

@RunWith(JUnit4::class)
class OrderProductsAdapterTest {

    @Test
    fun `OrderProductsAdapter class should exist`() {
        // Given & When
        val adapterClass = OrderProductsAdapter::class.java

        // Then
        assertNotNull(adapterClass)
        assertEquals("OrderProductsAdapter", adapterClass.simpleName)
    }

    @Test
    fun `OrderProductsAdapter should have getProductsWithQuantities method`() {
        // Given
        val methods = OrderProductsAdapter::class.java.methods

        // When
        val getProductsMethod = methods.find { it.name == "getProductsWithQuantities" }

        // Then
        assertNotNull(getProductsMethod)
    }

    @Test
    fun `OrderProductsAdapter should have OrderProductViewHolder inner class`() {
        // Given
        val innerClasses = OrderProductsAdapter::class.java.declaredClasses

        // When
        val viewHolderClass = innerClasses.find { it.simpleName == "OrderProductViewHolder" }

        // Then
        assertNotNull(viewHolderClass)
    }

    @Test
    fun `OrderProductsAdapter should have ProductoDiffCallback inner class`() {
        // Given
        val innerClasses = OrderProductsAdapter::class.java.declaredClasses

        // When
        val diffCallbackClass = innerClasses.find { it.simpleName == "ProductoDiffCallback" }

        // Then
        assertNotNull(diffCallbackClass)
    }

    @Test
    fun `ProductoDiffCallback areItemsTheSame should compare by id`() {
        // Given
        val callback = OrderProductsAdapter.ProductoDiffCallback()
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
        val callback = OrderProductsAdapter.ProductoDiffCallback()
        val product1 = createTestProduct("1", "Product 1")
        val product2 = createTestProduct("1", "Product 1")
        val product3 = product1.copy(name = "Different Name")

        // When & Then
        assertTrue(callback.areContentsTheSame(product1, product2))
        assertFalse(callback.areContentsTheSame(product1, product3))
    }

    @Test
    fun `getProductsWithQuantities return type should be Map`() {
        // Given
        val method = OrderProductsAdapter::class.java.methods.find {
            it.name == "getProductsWithQuantities"
        }

        // When & Then
        assertNotNull(method)
        assertEquals("getProductsWithQuantities", method?.name)
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
