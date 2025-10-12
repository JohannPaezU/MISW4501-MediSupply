package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.OrderListResponse
import com.mfpe.medisupply.data.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersViewModel : ViewModel() {

    private val orderRepository: OrderRepository = OrderRepository()

    fun getOrders(clientId : Int, sellerId : Int, onResult: (Boolean, String, OrderListResponse?) -> Unit) {
        orderRepository.getOrders(clientId, sellerId).enqueue(object :
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

}
