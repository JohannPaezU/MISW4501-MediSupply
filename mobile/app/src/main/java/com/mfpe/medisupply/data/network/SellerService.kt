package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.SellerHomeResponse
import com.mfpe.medisupply.data.model.SellerVisitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SellerService {

    @GET("seller/home")
    fun getHome(
        @Header("Authorization") authToken: String,
    ): Call<SellerHomeResponse>

    @GET("sellers/me/visits")
    fun getVisits(
        @Header("Authorization") authToken: String,
        @Query("date") date: String
    ): Call<SellerVisitResponse>

}