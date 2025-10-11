package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.repository.ProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsViewModel : ViewModel() {

    private val productRepository: ProductRepository = ProductRepository()
    private var currentProducts: List<Product> = emptyList()

    fun getProducts(onResult: (Boolean, String, ProductListResponse?) -> Unit) {
        productRepository.getProducts().enqueue(object :
            Callback<ProductListResponse> {
            override fun onResponse(call: Call<ProductListResponse>, res: Response<ProductListResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    currentProducts = res.body()?.products ?: emptyList()
                    onResult(true, "Products obtained.", res.body())
                } else {
                    onResult(false, "Error obtaining products: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

    fun getCurrentProducts(): List<Product> {
        return currentProducts
    }

}
