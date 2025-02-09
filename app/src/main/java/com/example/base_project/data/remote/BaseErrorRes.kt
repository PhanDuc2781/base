package com.example.base_project.data.remote

class BaseErrorRes<out E>(
    val title: String?,
    val summary: String?,
    val status: Int?,
    val errors: E?,
    val detail: DetailRes?
)

data class DetailRes(val message: String?)