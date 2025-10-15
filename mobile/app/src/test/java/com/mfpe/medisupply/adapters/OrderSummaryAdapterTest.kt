package com.mfpe.medisupply.adapters

import com.mfpe.medisupply.data.model.OrderSummaryItem
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrderSummaryAdapterTest {

    @Test
    fun `OrderSummaryAdapter class should exist`() {
        // Given & When
        val adapterClass = OrderSummaryAdapter::class.java

        // Then
        assertNotNull(adapterClass)
        assertEquals("OrderSummaryAdapter", adapterClass.simpleName)
    }

    @Test
    fun `OrderSummaryAdapter should have OrderSummaryViewHolder inner class`() {
        // Given
        val innerClasses = OrderSummaryAdapter::class.java.declaredClasses

        // When
        val viewHolderClass = innerClasses.find { it.simpleName == "OrderSummaryViewHolder" }

        // Then
        assertNotNull(viewHolderClass)
    }

    @Test
    fun `OrderSummaryAdapter should have OrderSummaryDiffCallback inner class`() {
        // Given
        val innerClasses = OrderSummaryAdapter::class.java.declaredClasses

        // When
        val diffCallbackClass = innerClasses.find { it.simpleName == "OrderSummaryDiffCallback" }

        // Then
        assertNotNull(diffCallbackClass)
    }

    @Test
    fun `OrderSummaryDiffCallback areItemsTheSame should compare by id`() {
        // Given
        val callback = OrderSummaryAdapter.OrderSummaryDiffCallback()
        val item1 = OrderSummaryItem("1", "Name", "url", 100.0, 1)
        val item2 = OrderSummaryItem("1", "Different Name", "url2", 200.0, 2)
        val item3 = OrderSummaryItem("2", "Name", "url", 100.0, 1)

        // When & Then
        assertTrue(callback.areItemsTheSame(item1, item2))
        assertFalse(callback.areItemsTheSame(item1, item3))
    }

    @Test
    fun `OrderSummaryDiffCallback areContentsTheSame should compare all fields`() {
        // Given
        val callback = OrderSummaryAdapter.OrderSummaryDiffCallback()
        val item1 = OrderSummaryItem("1", "Name", "url", 100.0, 1)
        val item2 = OrderSummaryItem("1", "Name", "url", 100.0, 1)
        val item3 = OrderSummaryItem("1", "Different", "url", 100.0, 1)

        // When & Then
        assertTrue(callback.areContentsTheSame(item1, item2))
        assertFalse(callback.areContentsTheSame(item1, item3))
    }

    @Test
    fun `OrderSummaryItem should be properly structured`() {
        // Given & When
        val item = OrderSummaryItem(
            id = "123",
            name = "Test Product",
            imageUrl = "https://example.com/image.jpg",
            price = 15000.0,
            quantity = 3
        )

        // Then
        assertEquals("123", item.id)
        assertEquals("Test Product", item.name)
        assertEquals(15000.0, item.price, 0.01)
        assertEquals(3, item.quantity)
    }
}
