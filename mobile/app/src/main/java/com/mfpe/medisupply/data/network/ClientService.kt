package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.model.VisitRequest
import com.mfpe.medisupply.data.model.VisitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ClientService {

    @GET("clients")
    fun getClients(
        @Header("Authorization") authToken: String
    ): Call<ClientListResponse>

    @POST("visits")
    fun createVisit(
        @Header("Authorization") authorization: String,
        @Body visitRequest: VisitRequest
    ): Call<VisitResponse>

}