package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.SellerHomeResponse
import retrofit2.Call
import retrofit2.http.GET

interface SellerService {

    @GET("seller/home")
    fun getHome(): Call<SellerHomeResponse>

}