package com.drew.themoviedatabase.data.repository.mymoviestv

import com.drew.themoviedatabase.data.remote.AddedToListResponse
import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.data.remote.MovieResponse
import com.drew.themoviedatabase.data.remote.MyAccountApiService
import com.drew.themoviedatabase.data.remote.TVShowsResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class MyMoviesTvRepository @Inject constructor(
    private val myAccountApiService: MyAccountApiService
) {


    private suspend fun fetchMovies(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,

        ) : List<com.drew.themoviedatabase.data.model.Movie?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiCall(pages)
                        if (response?.isSuccessful == true) {
                            response.body()?.getResults()
                        } else {
                            null
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchMovieDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?
    ) : List<MovieDetailsReleaseData?>? {
        return try {
            val movies = fetchMovies(pages = pages, apiCall = apiCall)
            if (movies != null) {
                coroutineScope {
                    val detailedMovies = movies.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = myAccountApiService.getMovieDetailsWithCertifications(movie?.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

                                if (response?.isSuccessful == true) {
                                    response.body()
                                } else {
                                    null
                                }
                            } catch (e:Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                    detailedMovies.awaitAll()
                }
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchTVShows(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,

        ) : List<com.drew.themoviedatabase.data.model.TVShow?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiCall(pages)
                        if (response?.isSuccessful == true) {
                            response.body()?.getTVShows()
                        } else {
                            null
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchTVShowDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?
    ) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            val shows = fetchTVShows(pages = pages, apiCall = apiCall)
            if (shows != null) {
                coroutineScope {
                    val tvshow = shows.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = myAccountApiService.getTVShowDetailsWithContentRatings(
                                    movie?.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

                                if (response?.isSuccessful == true) {
                                    response.body()
                                } else {
                                    null
                                }
                            } catch (e:Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                    tvshow.awaitAll()
                }
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchRatedMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyRatedMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchRatedTVShows(pages: Int, accountId: Int, sessionId: String?) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyRatedTV(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchFavoriteMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyFavoriteMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchFavoriteTVShows(pages: Int, accountId: Int, sessionId: String?) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyFavoriteTV(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchWatchlistMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyWatchListMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchWatchlistTVShows(pages: Int, accountId: Int, sessionId: String?) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService
                    .getMyWatchlistTV(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page,
                        accountId = accountId, sessionId = sessionId)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun addToFavoriteOrWatchlist(
        mediaType: String,
        mediaID: Int,
        listType: String,
        addToList: Boolean,
        accountId: Int,
        sessionId: String?
    ) : AddedToListResponse? {
        val jsonBody = JsonObject().apply {
            addProperty("media_type", mediaType)
            addProperty("media_id", mediaID)
            addProperty(listType, addToList)
        }
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        if (listType == "watchlist") {
                            val response = myAccountApiService.addToWatchlist(
                                accountId = accountId,
                                apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                                jsonBody = jsonBody,
                                sessionId = sessionId).execute()

                            if (response.isSuccessful) {
                                response.body()
                            } else {
                                null
                            }
                        } else {
                            val response = myAccountApiService.addFavorite(
                                accountId = accountId,
                                apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                                jsonBody = jsonBody,
                                sessionId = sessionId).execute()

                            if (response.isSuccessful) {
                                response.body()
                            } else {
                               null
                            }
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }
}