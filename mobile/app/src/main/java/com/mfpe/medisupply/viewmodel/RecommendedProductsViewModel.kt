package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.network.ClientService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecommendedProductsViewModel : ViewModel() {

    private val clientService = RetrofitApiClient.createRetrofitService(ClientService::class.java)

    fun getRecommendedProducts(
        authToken: String,
        onResult: (Boolean, String, List<Product>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = clientService.getRecommendedProducts(
                    "Bearer $authToken"
                ).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val products = response.body()!!.products
                        onResult(true, "Productos obtenidos correctamente", products)
                    } else {
                        onResult(false, "Error al cargar productos: ${response.code()}", null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, "Error de conexión: ${e.message}", null)
                }
            }
        }
    }
}
