package com.drew.themoviedatabase.POJO

import com.google.gson.annotations.SerializedName

class Trailers(
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("site")
    val site: String? = null,
    @SerializedName("type")
    val type: String? = null
)