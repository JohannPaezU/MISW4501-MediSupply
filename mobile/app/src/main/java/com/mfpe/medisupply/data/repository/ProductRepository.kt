package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.network.ProductService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import retrofit2.Call

class ProductRepository {
    private val productService: ProductService by lazy {
        RetrofitApiClient.createRetrofitService(ProductService::class.java)
    }

    fun getProducts(): Call<ProductListResponse> {
        return productService.getProducts()
    }

}