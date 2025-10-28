package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.OrderListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OrderService {

    @GET("order")
    fun getOrders(
        @Header("Authorization") authToken: String,
        @Query("client_id") clientId: String,
        @Query("seller_id") sellerId: String
    ): Call<OrderListResponse>

}