package com.mfpe.medisupply.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.repository.ProductRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendedProductsViewModel : ViewModel() {

    private val productRepository = ProductRepository()

    fun getRecommendedProducts(
        authToken: String,
        clientId: String,
        onResult: (Boolean, String, List<Product>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("RecommendedProductsViewModel", "Getting recommended products for client: $clientId")
                val response = productRepository.getRecommendedProducts(
                    authToken,
                    clientId
                ).execute()

                Log.d("RecommendedProductsViewModel", "Response: ${response.body()}")

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
