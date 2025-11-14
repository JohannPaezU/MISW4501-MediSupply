package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ProductListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProductService {

    @GET("products")
    fun getProducts(
        @Header("Authorization") authToken: String
    ): Call<ProductListResponse>

    @GET("products/recommended")
    fun getRecommendedProducts(
        @Header("Authorization") authToken: String,
        @Query("client_id") clientId: String
    ): Call<ProductListResponse>

}