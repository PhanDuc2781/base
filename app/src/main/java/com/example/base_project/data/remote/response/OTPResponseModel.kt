package com.example.base_project.data.remote.response

data class OTPResponseModel(
    val status_code: Int,
    val message: String,
    val data: Boolean
)