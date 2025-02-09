package com.example.base_project.data.remote

import com.example.base_project.data.remote.request.OTPRequest
import com.example.base_project.data.remote.response.OTPResponseModel

suspend fun ApiRequest.requestOTP(data: OTPRequest) = requestResult<OTPResponseModel>(
    ApiRouter(
        ApiPath.requestOtp(),
        HTTPMethod.POST,
        decodable = data,
        needLogin = false,
        mockFilePath = null
    ), shouldCheckError = true
)


