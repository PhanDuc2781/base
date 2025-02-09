package com.example.base_project.data.remote.response

data class LoginResponseModel(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Int?
)

fun LoginResponseModel.getAccessToken(): String? = access_token?.let { "Bearer $it" }
