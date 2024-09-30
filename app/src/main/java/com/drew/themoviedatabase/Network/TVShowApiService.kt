package com.drew.themoviedatabase.Network

import com.drew.themoviedatabase.POJO.Certifications
import com.drew.themoviedatabase.POJO.SeasonResponse
import com.drew.themoviedatabase.POJO.TVShowDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TVShowApiService {
    //V3 TV
    @GET("tv/airing_today")
    fun getTVShowsAiringToday(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query ("page") page: Int = 1
    ): Call<TVShowsResponse?>?

    @GET("tv/on_the_air")
    fun getTVShowsOnTheAir(
        @Query("language") language: String,
        @Query("api_key") apiKey: String,
        @Query ("page") page: Int = 1): Call<TVShowsResponse?>?

    @GET("tv/popular")
    fun getPopularTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String,
        @Query ("page") page: Int = 1
    ): Call<TVShowsResponse?>?

    @GET("tv/top_rated")
    fun getTopRatedTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query ("page") page: Int = 1): Call<TVShowsResponse?>?

    @GET("trending/tv/{time_window}")
    fun getTrendingTVShows(
        @Query("language") language: String,
        @Query("api_key") apiKey: String): Call<TVShowsResponse?>?

    @GET("tv/{series_id}")
    fun getTVShowDetails(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<TVShowDetails?>?

    @GET("tv/{series_id}")
    fun getTVShowDetailsWithContentRatings(
        @Path("series_id") tvShowId: Int?,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query("append_to_response") appendToResponse: String = "content_ratings,watch/providers"): Call<TVShowDetails?>?

    @GET("tv/{series_id}")
    fun getTVShowDetailsWithCastAndVideos(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query("append_to_response") appendToResponse: String = "credits,videos,content_ratings,watch/providers"): Call<TVShowDetailsWithCastAndVideos?>?

    @GET("tv/{series_id}/videos")
    fun getTVShowTrailer(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<TrailersResponse?>?

    @GET("tv/{series_id}/reviews")
    fun getTVShowReviews(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query ("page") page: Int): Call<ReviewsResponse?>?

    @GET("tv/{series_id}/similar")
    fun getSimilarTVShows(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?,
        @Query ("page") page: Int = 1): Call<TVShowsResponse?>?

    @GET("certification/tv/list")
    fun getCertifications(@Query("api_key") apiKey: String): Call <Certifications?>?

    @GET("tv/{series_id}/recommendations")
    fun getRecommendedTVShows(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<TVShowsResponse?>?

    @GET("tv/{series_id}/images")
    fun getTVShowImages(
        @Path("series_id") movieId: Int,
        @Query("include_image_language") imageLanguage: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieImagesResponse?>?

    @GET("tv/{series_id}/watch/providers")
    fun getTVShowProviders(
        @Path("series_id") tvShowId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<TVProvidersResponse?>?

    @GET("{series_id}/season/{season_number}")
    fun getSeasonDetails(
        @Path("series_id") tvShowId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<SeasonResponse?>?

    @GET("/tv/{series_id}/season/{season_number}/credits")
    fun getSeasonCast(
        @Path("series_id") tvShowId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String?): Call<CastResponse?>?

    @GET("tv/popular")
    fun getTotalPagesPopular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("tv/top_rated")
    fun getTotalPagesTopRated(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("tv/airing_today")
    fun getTotalPagesAiringToday(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?

    @GET("tv/on_the_air")
    fun getTotalPagesOnTheAir(
        @Query("api_key") apiKey: String,
        @Query("language") language: String): Call <TotalPages?>?
}