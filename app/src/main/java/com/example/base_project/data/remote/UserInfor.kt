package com.example.base_project.data.remote

import com.example.base_project.util.StorageEncodable

@StorageEncodable
data class UserData(
    val member_code: String? = null,
    val name: String? = null,
    val email: String? = null,
)
