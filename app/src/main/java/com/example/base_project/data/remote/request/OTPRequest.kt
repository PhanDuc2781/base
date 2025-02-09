package com.example.base_project.data.remote.request

import com.example.base_project.data.remote.Decodable


data class OTPRequest(val email: String, val type: String, val workspace_id: Int = 0) : Decodable