package com.drew.themoviedatabase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_tvshows")
data class MyWatchlistTVShows(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val seriesId: Int?,
    val seriesTitle: String?,
    val seriesPoster: String?
)
