package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.CreateOrderRequest
import com.mfpe.medisupply.data.model.CreateOrderResponse
import com.mfpe.medisupply.data.model.OrderListResponse
import com.mfpe.medisupply.data.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    fun getOrders(authToken: String, clientId : String, sellerId : String, onResult: (Boolean, String, OrderListResponse?) -> Unit) {
        orderRepository.getOrders(authToken, clientId, sellerId).enqueue(object :
            Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, res: Response<OrderListResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Orders obtained.", res.body())
                } else {
                    onResult(false, "Error obtaining orders: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

    fun createOrder(authToken: String, orderRequest: CreateOrderRequest, onResult: (Boolean, String, CreateOrderResponse?) -> Unit) {
        orderRepository.createOrder(authToken, orderRequest).enqueue(object :
            Callback<CreateOrderResponse> {
            override fun onResponse(call: Call<CreateOrderResponse>, res: Response<CreateOrderResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Order created successfully.", res.body())
                } else {
                    val errorMessage = "Error creating order: ${res.code()} - ${res.message()}"
                    onResult(false, errorMessage, null)
                }
            }

            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

}
