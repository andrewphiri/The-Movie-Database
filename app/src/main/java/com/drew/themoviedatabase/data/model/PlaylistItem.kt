package com.drew.themoviedatabase.data.model

import com.google.gson.annotations.SerializedName

data class PlaylistItem(
    @SerializedName("contentDetails") val contentDetails: ContentDetails
)

data class ContentDetails(
    @SerializedName("relatedPlaylists") val relatedPlaylists: RelatedPlaylists
)

data class RelatedPlaylists(
    @SerializedName("uploads") val uploads: String
)
