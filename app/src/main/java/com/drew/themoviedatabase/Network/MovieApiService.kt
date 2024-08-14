package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.MovieProviders
import com.drew.themoviedatabase.POJO.SeasonResponse
import com.drew.themoviedatabase.POJO.TVShowDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    //V3 Movies
    @GET("movie/upcoming")
    fun getTotalPagesUpcoming(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query ("page") page: Int = 1): Call <MovieResponse?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String,
        @Query ("page") page: Int = 1): Call<MovieResponse?>?

    @GET("movie/top_rated")
    fun getTotalPagesTopRated(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query ("page") page: Int = 1): Call <MovieResponse?>?

    @GET("movie/now_playing")
    fun getTotalPagesNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query ("page") page: Int = 1): Call <MovieResponse?>?

    @GET("movie/popular")
    fun getTotalPagesPopular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("trending/movie/{time_window}")
    fun getTrendingMovies(
        @Path ("time_window") timeWindow: String = "day",
        @Query("language") language: String,
        @Query("api_key") apiKey: String, ): Call <MovieResponse?>?


    @GET("movie/{id}")
   fun getMovieDetails(
        @Path("id") movieId: Int?,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call <MovieDetails?>?

   @GET("movie/{id}")
   fun getMovieDetailsWithCertifications(
      @Path("id") movieId: Int?,
      @Query("api_key") apiKey: String?,
      @Query("language") language: String,
      @Query("append_to_response") appendToResponse: String = "release_dates"): Call <MovieDetailsReleaseData?>?

    @GET("movie/{id}")
    fun getMovieDetailsWithCastAndVideos(
       @Path("id") movieId: Int,
       @Query("api_key") apiKey: String,
       @Query("language") language: String,
       @Query("append_to_response") appendToResponse: String = "credits,videos,release_dates"
    ): Call<MovieDetailsResponse?>

    @GET("movie/{id}/videos")
    fun getTrailer(
        @Path("id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call <TrailersResponse?>?

    @GET("movie/{id}/reviews")
    fun getReviews(
        @Path("id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?) : Call <ReviewsResponse?>?


    @GET("movie/{movie_id}/release_dates")
    fun getReleaseDateAndCertification(
        @Path("movie_id") movieId: Int?,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<MovieReleaseData?>?

    @GET("movie/{id}/similar")
    fun getSimilarMovies(
        @Path("id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query ("page") page: Int = 1): Call<MovieResponse?>?

    @GET("movie/{movie_id}/credits")
    fun getCast(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<CastResponse?>?

    @GET("movie/{id}/watch/providers")
    fun getMovieProviders(
        @Path("id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<MovieProviders?>?

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendedMovies(
        @Path("id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call <MovieResponse?>?



    //V4 endpoints

    @GET("movie/recommendations")
    fun getRecommendationMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("movie/rated")
    fun getRatedMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("movie/favorites")
    fun getFavoriteMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("tv/favorites")
    fun getFavoriteTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("tv/rated")
    fun getRatedTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("tv/recommendations")
    fun getRecommendationTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("tv/watchlist")
    fun getWatchlistTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?

    @GET("movie/watchlist")
    fun getWatchlistMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call <MovieResponse?>?
}