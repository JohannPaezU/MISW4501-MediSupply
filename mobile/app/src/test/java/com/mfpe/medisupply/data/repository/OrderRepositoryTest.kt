package com.mfpe.medisupply.data.repository

import org.junit.Assert.assertNotNull
import org.junit.Test

class OrderRepositoryTest {
    private val orderRepository = OrderRepository()

    @Test
    fun `getOrders should return Call with correct type`() {
        val result = orderRepository.getOrders(clientId = 1, sellerId = 2)
        assertNotNull(result)
    }
}
