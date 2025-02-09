package com.example.base_project.data.remote

import com.example.base_project.applicattion.activeActivity
import com.example.base_project.applicattion.isInternetAvailable
import com.example.base_project.applicattion.storage
import com.example.base_project.data.remote.ApiRequest.Companion.isMock
import com.google.gson.Gson
import kotlinx.coroutines.delay

data class ApiRouter(
    val path: Path,
    val method: HTTPMethod = HTTPMethod.GET,
    val parameters: Parameters = hashMapOf(),
    val headers: Headers = JsonFormatter,
    val needLogin: Boolean = true,
    val mockFilePath: String? = null
) {
    constructor(
        path: String,
        method: HTTPMethod = HTTPMethod.GET,
        decodable: Decodable? = null,
        headers: Headers = JsonFormatter,
        needLogin: Boolean = true,
        mockFilePath: String? = null
    ) : this(path, method, decodable?.decode() ?: hashMapOf(), headers, needLogin, mockFilePath)

    constructor(
        path: String,
        method: HTTPMethod = HTTPMethod.GET,
        headers: Headers = JsonFormatter,
        needLogin: Boolean = true,
        mockFilePath: String? = null
    ) : this(path, method, hashMapOf(), headers, needLogin, mockFilePath)
}

fun ApiRouter.url(): String =
    if (path.contains("https") || path.contains("http")) path else ApiRequest.BASE_URL + path

val AuthorizationHeader: Pair<String, String>
    get() = "Authorization" to storage.accessToken

val JsonFormatter = hashMapOf("accept" to "application/json", "Content-Type" to "application/json")

suspend inline fun <reified T> ApiRouter.mockResponse(gson: Gson): RepositoryResult<T>? {
    if (isMock && !mockFilePath.isNullOrEmpty()) {
        if (!isInternetAvailable) {
            return RepositoryResult.Error(NoInternet)
        }
        delay(500)
        val error = RepositoryResult.Error(Throwable("File not found"))
        try {
            val am = activeActivity()?.assets ?: return error
            val mockFile = am.open(mockFilePath).bufferedReader().use {
                it.readText()
            }

            return if (T::class.isInstance(true)) {
                RepositoryResult.Success(mockFile.isNotEmpty() as T)
            } else {
                val result: T = gson.fromJson(mockFile)
                RepositoryResult.Success(result)
            }
        } catch (e: Exception) {
            return error
        }
    }
    return null
}

suspend inline fun <reified T, reified U> Pair<ApiRouter, ApiRouter>.mockResponse(gson: Gson): RepositoryResult<Pair<T, U>>? {
    if (isMock && !first.mockFilePath.isNullOrEmpty() && !second.mockFilePath.isNullOrEmpty()) {
        if (!isInternetAvailable) {
            return RepositoryResult.Error(NoInternet)
        }
        delay(500)
        val error = RepositoryResult.Error(Throwable("File not found"))
        try {
            val am = activeActivity()?.assets ?: return error
            val mockFile1 = am.open(first.mockFilePath!!).bufferedReader().use {
                it.readText()
            }
            val mockFile2 = am.open(second.mockFilePath!!).bufferedReader().use {
                it.readText()
            }
            val result1: T = gson.fromJson(mockFile1)
            val result2: U = gson.fromJson(mockFile2)
            return RepositoryResult.Success(Pair(result1, result2))
        } catch (e: Exception) {
            return error
        }
    }
    return null
}

suspend inline fun <reified T, reified U, reified Y> Triple<ApiRouter, ApiRouter, ApiRouter>.mockResponse(
    gson: Gson
): RepositoryResult<Triple<T, U, Y>>? {
    if (isMock && !first.mockFilePath.isNullOrEmpty() && !second.mockFilePath.isNullOrEmpty()) {
        if (!isInternetAvailable) {
            return RepositoryResult.Error(NoInternet)
        }
        delay(500)
        val error = RepositoryResult.Error(Throwable("File not found"))
        try {
            val am = activeActivity()?.assets ?: return error
            val mockFile1 = am.open(first.mockFilePath!!).bufferedReader().use {
                it.readText()
            }
            val mockFile2 = am.open(second.mockFilePath!!).bufferedReader().use {
                it.readText()
            }
            val mockFile3 = am.open(second.mockFilePath!!).bufferedReader().use {
                it.readText()
            }
            val result1: T = gson.fromJson(mockFile1)
            val result2: U = gson.fromJson(mockFile2)
            val result3: Y = gson.fromJson(mockFile3)
            return RepositoryResult.Success(Triple(result1, result2, result3))
        } catch (e: Exception) {
            return error
        }
    }
    return null
}

