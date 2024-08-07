package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName


class Reviews(
    @SerializedName("content")
    val content: String,
    @SerializedName("author")
    private var author: String?,
    @SerializedName("created_at")
    val date: String,
    @SerializedName("username")
    private val userName: String? = null
)