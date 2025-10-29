package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.SellerHomeResponse
import com.mfpe.medisupply.data.model.SellerVisitResponse
import com.mfpe.medisupply.data.network.RetrofitApiClient
import com.mfpe.medisupply.data.network.SellerService
import retrofit2.Call

class SellerRepository {
    private val sellerService: SellerService by lazy {
        RetrofitApiClient.createRetrofitService(SellerService::class.java)
    }

    fun getHome(authToken: String): Call<SellerHomeResponse> {
        return sellerService.getHome(authToken)
    }

    fun getVisits(authToken: String, date: String): Call<SellerVisitResponse> {
        return sellerService.getVisits(authToken, date)
    }

}