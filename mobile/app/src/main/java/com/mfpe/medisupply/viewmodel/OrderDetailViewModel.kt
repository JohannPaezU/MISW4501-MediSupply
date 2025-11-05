package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.OrderDetailResponse
import com.mfpe.medisupply.data.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    fun getOrderDetail(
        authToken: String,
        orderId: String,
        onResult: (Boolean, String, OrderDetailResponse?) -> Unit
    ) {
        orderRepository.getOrderDetail(authToken, orderId).enqueue(object :
            Callback<OrderDetailResponse> {
            override fun onResponse(
                call: Call<OrderDetailResponse>,
                response: Response<OrderDetailResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(true, "Orden obtenida correctamente", response.body())
                } else {
                    onResult(false, "Error al cargar la orden: ${response.code()}", null)
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                onResult(false, "Error de conexión: ${t.message}", null)
            }
        })
    }
}

