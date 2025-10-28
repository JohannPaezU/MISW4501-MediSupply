package com.mfpe.medisupply.data.repository

import android.util.Log
import com.mfpe.medisupply.data.model.CreateOrderRequest
import com.mfpe.medisupply.data.model.CreateOrderResponse
import com.mfpe.medisupply.data.model.OrderListResponse
import com.mfpe.medisupply.data.network.OrderService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import retrofit2.Call

class OrderRepository {
    private val orderService: OrderService by lazy {
        RetrofitApiClient.createRetrofitService(OrderService::class.java)
    }

    fun getOrders(authToken: String, clientId : String, sellerId : String): Call<OrderListResponse> {
        return orderService.getOrders(authToken, clientId, sellerId)
    }

    fun createOrder(authToken: String, orderRequest: CreateOrderRequest): Call<CreateOrderResponse> {

        return orderService.createOrder(authToken, orderRequest)
    }

}