package com.mfpe.medisupply.data.model

import java.io.Serializable

data class ProductListResponse(
    val products: List<Product>
): Serializable

