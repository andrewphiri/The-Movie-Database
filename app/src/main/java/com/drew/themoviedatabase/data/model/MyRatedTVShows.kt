package com.drew.themoviedatabase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rated_tvshows")
data class MyRatedTVShows(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val seriesId: Int?,
    val seriesTitle: String?,
    val seriesPoster: String?
)
