package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.SellerHomeResponse
import com.mfpe.medisupply.data.repository.SellerRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SellerViewModel(
    private val sellerRepository: SellerRepository = SellerRepository()
) : ViewModel() {

    fun getHome(authToken: String, onResult: (Boolean, String, SellerHomeResponse?) -> Unit) {
        sellerRepository.getHome(authToken).enqueue(object :
            Callback<SellerHomeResponse> {
            override fun onResponse(call: Call<SellerHomeResponse>, res: Response<SellerHomeResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Seller home obtained.", res.body())
                } else {
                    onResult(false, "Error obtaining seller home: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<SellerHomeResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

}
