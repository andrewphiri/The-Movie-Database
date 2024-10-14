package com.drew.themoviedatabase.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rated_movies")
data class MyRatedMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movieId: Int?,
    val movieTitle: String?,
    val moviePoster: String?
)
