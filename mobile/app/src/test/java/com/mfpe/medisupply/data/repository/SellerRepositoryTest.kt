package com.mfpe.medisupply.data.repository

import org.junit.Assert.assertNotNull
import org.junit.Test

class SellerRepositoryTest {
    private val sellerRepository = SellerRepository()

    @Test
    fun `getSellerHome should return Call with correct type`() {
        val result = sellerRepository.getHome()
        assertNotNull(result)
    }
}

