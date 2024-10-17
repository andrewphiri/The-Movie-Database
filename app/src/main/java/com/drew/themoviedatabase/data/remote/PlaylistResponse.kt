package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.model.VideoItems

data class PlaylistResponse(
    val nextPageToken: String?,
    val items: List<VideoItems>
)
