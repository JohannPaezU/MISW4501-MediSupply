package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.CenterListResponse
import com.mfpe.medisupply.data.repository.DistributionCenterRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistributionCenterViewModel(
    private val distributionCenterRepository: DistributionCenterRepository = DistributionCenterRepository()
) : ViewModel() {

    fun getDistributionCenters(authToken: String, onResult: (Boolean, String, CenterListResponse?) -> Unit) {
        distributionCenterRepository.getDistributionCenters(authToken).enqueue(object :
            Callback<CenterListResponse> {
            override fun onResponse(call: Call<CenterListResponse>, res: Response<CenterListResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Centers obtained.", res.body())
                } else {
                    onResult(false, "Error obtaining centers: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<CenterListResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

}
