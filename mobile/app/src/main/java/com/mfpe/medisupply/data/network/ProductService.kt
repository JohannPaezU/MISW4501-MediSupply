package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ProductListResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductService {

    @GET("products")
    fun getProducts(): Call<ProductListResponse>

}