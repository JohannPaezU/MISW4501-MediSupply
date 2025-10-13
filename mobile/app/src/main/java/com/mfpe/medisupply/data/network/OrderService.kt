package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.OrderListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OrderService {

    @GET("order")
    fun getOrders(
        @Query("client_id") clientId: Int,
        @Query("seller_id") sellerId: Int
    ): Call<OrderListResponse>

}