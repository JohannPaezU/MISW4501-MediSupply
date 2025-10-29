package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.model.ProductListResponse
import com.mfpe.medisupply.data.model.VisitRequest
import com.mfpe.medisupply.data.model.VisitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClientService {

    @GET("sellers/me/clients")
    fun getClients(
        @Header("Authorization") authToken: String
    ): Call<ClientListResponse>

    @GET("clients/me/recommended-products")
    fun getRecommendedProducts(
        @Header("Authorization") authorization: String
    ): Call<ProductListResponse>

    @POST("clients/me/visits")
    fun createVisit(
        @Header("Authorization") authorization: String,
        @Body visitRequest: VisitRequest
    ): Call<VisitResponse>

}