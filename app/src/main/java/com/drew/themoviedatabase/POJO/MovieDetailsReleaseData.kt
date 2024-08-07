package com.drew.themoviedatabase.POJO

import com.drew.themoviedatabase.Network.MovieReleaseData

data class MovieDetailsReleaseData(
    val movieDetails: List<MovieDetails?>?,
    val movieReleaseData: List<MovieReleaseData?>?
)
