package com.example.base_project.data.remote

typealias Path = String

object ApiPath {
    fun login(): Path = "login"
    fun refreshToken(): Path = "refresh"
    fun unregisterToken(): Path = "unregister-token"
    fun requestOtp(): Path = "auth/otp"
}