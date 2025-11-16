package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.network.ProductService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import retrofit2.Call
import android.util.Log

class ProductRepository {
    private val productService: ProductService by lazy {
        RetrofitApiClient.createRetrofitService(ProductService::class.java)
    }

    fun getProducts(authToken: String): Call<ProductListResponse> {
        return productService.getProducts(authToken)
    }

    fun getRecommendedProducts(authToken: String, clientId: String): Call<ProductListResponse> {
        return productService.getRecommendedProducts(authToken, clientId)
    }

}