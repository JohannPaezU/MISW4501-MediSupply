package com.mfpe.medisupply.data.repository

import org.junit.Assert.assertNotNull
import org.junit.Test

class ProductRepositoryTest {
    private val productRepository = ProductRepository()

    @Test
    fun `getProducts should return Call with correct type`() {
        val result = productRepository.getProducts()
        assertNotNull(result)
    }

}

