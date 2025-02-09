package com.example.base_project.data.remote

enum class HTTPError(val code: Int) {
    UNAUTHORISE(401),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    UNPROCESSABLE_CONTENT(422);

    companion object {
        fun get(code: Int) = entries.firstOrNull { it.code == code }

        fun from(throwable: Throwable) = throwable.httpCode()?.let { return@let get(it) }
    }
}