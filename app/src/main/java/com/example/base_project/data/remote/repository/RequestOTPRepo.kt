package com.example.base_project.data.remote.repository

import com.example.base_project.data.remote.ApiRequest
import com.example.base_project.data.remote.request.OTPRequest
import com.example.base_project.data.remote.requestOTP
import com.example.base_project.data.remote.response.OTPResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface RequestOTPRepo {
    suspend fun requestOtp(data: OTPRequest): Result<OTPResponseModel?>
}

@Singleton
class RequestOTPRepoImpl @Inject constructor(private val apiRequest: ApiRequest) : RequestOTPRepo {
    override suspend fun requestOtp(data: OTPRequest): Result<OTPResponseModel?> = withContext(Dispatchers.IO){
        apiRequest.requestOTP(data)
    }

}