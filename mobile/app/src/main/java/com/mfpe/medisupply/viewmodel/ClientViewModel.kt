package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.ClientListResponse
import com.mfpe.medisupply.data.repository.ClientRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientViewModel(
    private val clientRepository: ClientRepository = ClientRepository()
) : ViewModel() {

    fun getClients(authToken : String, onResult: (Boolean, String, ClientListResponse?) -> Unit) {
        clientRepository.getClients(authToken).enqueue(object :
            Callback<ClientListResponse> {
            override fun onResponse(call: Call<ClientListResponse>, res: Response<ClientListResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Clients obtained.", res.body())
                } else {
                    onResult(false, "Error obtaining clients: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<ClientListResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

}
