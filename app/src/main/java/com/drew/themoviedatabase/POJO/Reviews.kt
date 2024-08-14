package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName


data class Reviews(
    @SerializedName("content")
    val content: String,
    @SerializedName("author")
    var author: String?,
    @SerializedName("author_details")
    var authorDetails: AuthorDetails,
    @SerializedName("created_at")
    val date: String,
)

data class AuthorDetails(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("avatar_path")
    val avatarPath: String,
)