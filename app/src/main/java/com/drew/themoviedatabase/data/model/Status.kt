package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("privacyStatus") val privacyStatus: String
)
