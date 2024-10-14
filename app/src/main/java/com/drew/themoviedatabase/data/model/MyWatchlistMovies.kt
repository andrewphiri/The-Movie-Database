package com.drew.themoviedatabase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_movies")
data class MyWatchlistMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movieId: Int?,
    val movieTitle: String?,
    val moviePoster: String?
)
