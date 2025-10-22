package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.model.ProductListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ClientService {

    @GET("clients")
    fun getClients(
        @Header("Authorization") authToken: String,
        @Query("seller_id") sellerId: String,
    ): Call<ClientListResponse>

    @GET("clients/{clientId}/recommended-products")
    fun getRecommendedProducts(
        @Path("clientId") clientId: String,
        @Header("Authorization") authorization: String
    ): Call<ProductListResponse>

}