package com.aichat.app.data.remote.model

data class ErrorResponse(
    val error: ErrorBody? = null,
)

data class ErrorBody(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null,
)
