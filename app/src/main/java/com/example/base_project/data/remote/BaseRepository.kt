package com.example.base_project.data.remote

import com.example.base_project.applicattion.activeActivity
import com.google.gson.Gson
import okhttp3.ResponseBody

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T?) : RepositoryResult<T>()
    data class Error(val error: Throwable) : RepositoryResult<Nothing>()
}

data object NoInternet : Throwable()
data object ServerError : Throwable()
data object TimeOutError : Throwable()
data class OtherError(val throwable: Throwable, val body: ResponseBody?) : Throwable()

inline fun <reified E> OtherError.parseError(gson: Gson): E? {
    return try {
        body?.string()?.run {
            gson.fromJson(this, E::class.java)
        }
    } catch (e: Exception) {
        null
    }
}

inline fun <reified E> Throwable.parseError(gson: Gson): E? {
    return (this as? OtherError)?.parseError(gson)
}

inline fun <T, reified E> Result<T>.onFailureDecode(
    gson: Gson, action: (exception: BaseErrorRes<E>?) -> Unit
): Result<T> {
    return this.onFailure {
        action.invoke(it.parseError<BaseErrorRes<E>>(gson))
    }
}

fun <T> RepositoryResult<T>.handleDefaultError(
    shouldCheckError: Boolean = true
): Result<T?> {
    return when (this) {
        is RepositoryResult.Error -> {
            when (val errorType = this.error) {
                is NoInternet, is ServerError, is TimeOutError -> {
                    return if (shouldCheckError) {
                        activeActivity()?.let {
                            /*it.showCustomAlertDialog(
                                AlertData(
                                    title = "エラーが発生しました", it.getString(
                                        if (errorType is NoInternet) R.string.connectErrorMsg
                                        else R.string.serverErrorMsg
                                    ), it.getString(R.string.cancelMsg)
                                )
                            )*/
                        }
                        return Result.failure(errorType)
                    } else {
                        Result.failure(errorType)
                    }
                }

                is OtherError -> Result.failure(errorType)

                else -> Result.failure(errorType)
            }
        }

        is RepositoryResult.Success -> Result.success(this.data)
    }
}