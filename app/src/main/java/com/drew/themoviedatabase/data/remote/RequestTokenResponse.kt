package com.drew.themoviedatabase.data.remote

data class RequestTokenResponse(
    val success: Boolean,
    val expires_at: String,
    val request_token: String
)
