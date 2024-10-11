package com.drew.themoviedatabase.data.remote

import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MyAccountApiService {

    @GET("movie/{id}")
    fun getMovieDetailsWithCertifications(
        @Path("id") movieId: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String = "release_dates,watch/providers"): Call <MovieDetailsReleaseData?>?

    @GET("tv/{series_id}")
    fun getTVShowDetailsWithContentRatings(
        @Path("series_id") tvShowId: Int?,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query("append_to_response") appendToResponse: String = "content_ratings,watch/providers"): Call<com.drew.themoviedatabase.data.model.TVShowDetails?>?

    @GET("account/{account_id}/favorite/movies")
    fun getMyFavoriteMovies(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<MovieResponse?>?

    @GET("account/{account_id}/favorite/tv")
    fun getMyFavoriteTV(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<TVShowsResponse?>?

    @GET("account/{account_id}/rated/movies")
    fun getMyRatedMovies(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<MovieResponse?>?

    @GET("account/{account_id}/rated/tv")
    fun getMyRatedTV(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<TVShowsResponse?>?

    @GET("account/{account_id}/watchlist/movies")
    fun getMyWatchListMovies(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<MovieResponse?>?

    @GET("account/{account_id}/watchlist/tv")
    fun getMyWatchlistTV(
        @Path("account_id") accountId: Int = 21411766,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String,
        @Query("page") page: Int = 1): Call<TVShowsResponse?>?

    @POST("account/{account_id}/favorite")
    fun addFavorite(
        @Path("account_id") accountId: Int = 21411766,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<AddedToListResponse>

    @POST("account/{account_id}/watchlist")
    fun addToWatchlist(
        @Path("account_id") accountId: Int = 21411766,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<AddedToListResponse>

    @POST("movie/{movie_id}/rating")
    fun addMovieRating(
        @Path("movie_id") movieID: Int?,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<Boolean>

    @DELETE("movie/{movie_id}/rating")
    fun deleteMovieRating(
        @Path("movie_id") movieID: Int?,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<Boolean>

    @POST("tv/{series_id}/rating")
    fun addTVShowRating(
        @Path("series_id") seriesID: Int?,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<Boolean>

    @DELETE("tv/{series_id}/rating")
    fun deleteTVShowRating(
        @Path("series_id") seriesID: Int?,
        @Body jsonBody: JsonObject,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
    ) : Call<Boolean>
}