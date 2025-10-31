package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.SellerHomeResponse
import com.mfpe.medisupply.data.model.SellerVisitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SellerService {

    @GET("sellers/me")
    fun getHome(
        @Header("Authorization") authToken: String,
    ): Call<SellerHomeResponse>

    @GET("visits")
    fun getVisits(
        @Header("Authorization") authToken: String,
        @Query("expected_date") date: String
    ): Call<SellerVisitResponse>

}