package com.example.base_project.data.remote

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

typealias Parameters = HashMap<String, Any>
typealias Headers = HashMap<String, String>

interface ApiService : BaseApiService
interface AuthApiService : BaseApiService

interface BaseApiService {

    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @QueryMap parameterName: Parameters = hashMapOf()
    ): ResponseBody

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @Body parameters: Parameters = hashMapOf()
    ): ResponseBody

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @Body parameters: Parameters = hashMapOf()
    ): ResponseBody

    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @QueryMap parameters: Parameters = hashMapOf()
    ): ResponseBody
}

suspend inline fun BaseApiService.request(router: ApiRouter): ResponseBody {
    val parameters: Parameters = router.parameters
    return when (router.method) {
        HTTPMethod.GET -> get(router.url(), router.headers, parameters)
        HTTPMethod.POST -> post(router.url(), router.headers, parameters)
        HTTPMethod.PUT -> put(router.url(), router.headers, parameters)
        HTTPMethod.DELETE -> delete(router.url(), router.headers, parameters)
    }
}