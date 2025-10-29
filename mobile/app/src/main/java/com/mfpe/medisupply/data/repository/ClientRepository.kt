package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.model.VisitRequest
import com.mfpe.medisupply.data.model.VisitResponse
import com.mfpe.medisupply.data.network.ClientService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import retrofit2.Call

class ClientRepository {
    private val clientService: ClientService by lazy {
        RetrofitApiClient.createRetrofitService(ClientService::class.java)
    }

    fun getClients(authToken: String): Call<ClientListResponse> {
        return clientService.getClients(authToken)
    }

    fun createVisit(authToken: String, visitRequest: VisitRequest): Call<VisitResponse> {
        return clientService.createVisit(authToken, visitRequest)
    }

}