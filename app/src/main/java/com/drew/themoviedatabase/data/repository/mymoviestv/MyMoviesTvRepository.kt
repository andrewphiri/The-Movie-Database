package com.drew.themoviedatabase.data.repository.mymoviestv

import com.drew.themoviedatabase.data.remote.AddedToListResponse
import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.data.model.TVShowDetails
import com.drew.themoviedatabase.data.remote.API_KEY
import com.drew.themoviedatabase.data.remote.MovieResponse
import com.drew.themoviedatabase.data.remote.MyAccountApiService
import com.drew.themoviedatabase.data.remote.TVShowsResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class MyMoviesTvRepository @Inject constructor(
    private val myAccountApiService: MyAccountApiService
) {

    private fun fetchMovies(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (List<com.drew.themoviedatabase.data.model.Movie>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allMovies = mutableListOf<com.drew.themoviedatabase.data.model.Movie>()
                val jobs = (1..pages).map { page ->
                    async {
                        try {
                            val response = apiCall(page)
                            if (response?.isSuccessful == true) {
                                response.body()?.let { movieResponse ->
                                    movieResponse.getResults()?.let { allMovies.addAll(it) }
                                }
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                // Await all jobs to complete
                jobs.awaitAll()
                // Switch to the main thread to invoke the callback
                withContext(Dispatchers.Main) {
                    callback(allMovies)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    private fun fetchMovieDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (List<MovieDetailsReleaseData?>?) -> Unit,
    ) {
        try {
            fetchMovies(pages, apiCall) { movies ->
                if (movies != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedMovies = mutableListOf<MovieDetailsReleaseData?>()
                        val jobs = movies.map { movie ->
                            async {
                                try {
                                    val response = myAccountApiService.getMovieDetailsWithCertifications(movie.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

                                    if (response?.isSuccessful == true) {
                                        response.body()
                                    } else {
                                        null
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }

                            }
                        }

                        detailedMovies.addAll(jobs.awaitAll())
                        withContext(Dispatchers.Main) {
                            callback(detailedMovies)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        } catch (e: Exception) {
            callback(null)
            e.printStackTrace()
        }
    }

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

    private fun fetchTVShows(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,
        callback: (List<com.drew.themoviedatabase.data.model.TVShow>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allTVShows = mutableListOf<com.drew.themoviedatabase.data.model.TVShow>()
                val jobs = (1..pages).map { page ->
                    async {
                        try {
                            val response = apiCall(page)
                            if (response?.isSuccessful == true) {
                                response.body()?.let { tvResponse ->
                                    tvResponse.getTVShows()?.let { allTVShows.addAll(it) }
                                }
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                // Await all jobs to complete
                jobs.awaitAll()
                // Switch to the main thread to invoke the callback
                withContext(Dispatchers.Main) {
                    callback(allTVShows)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    private fun fetchTVShowDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,
        callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit,
    ) {
        try {
            fetchTVShows(pages, apiCall) { tvShows ->
                if (tvShows != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedTVShows = mutableListOf<com.drew.themoviedatabase.data.model.TVShowDetails?>()
                        val jobs = tvShows.map { tvShow ->
                            async {
                                try {
                                    val response = myAccountApiService.getTVShowDetailsWithContentRatings(tvShow.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

                                    if (response?.isSuccessful == true) {
                                        response.body()
                                    } else {
                                        null
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }

                            }
                        }

                        detailedTVShows.addAll(jobs.awaitAll())
                        withContext(Dispatchers.Main) {
                            callback(detailedTVShows)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        } catch (e: Exception) {
            callback(null)
            e.printStackTrace()
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

    //Optional functions to fetch my movies and shows


    fun fetchFavoriteMovies(pages: Int, accountId: Int, sessionId: String?, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyFavoriteMovies(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchWatchlistMovies(pages: Int, accountId: Int, sessionId: String?, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyWatchListMovies(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchRatedMovies(pages: Int, accountId: Int, sessionId: String?, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyFavoriteMovies(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchFavoriteTVShows(pages: Int, accountId: Int, sessionId: String?, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyFavoriteTV(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchWatchlistTVShows(pages: Int, accountId: Int, sessionId: String?, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyWatchlistTV(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchRatedTVShows(pages: Int, accountId: Int, sessionId: String?, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> myAccountApiService.getMyRatedTV(apiKey = API_KEY, accountId = accountId, sessionId = sessionId, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getTotalPagesFavoriteMovies(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyFavoriteMoviesTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesFavoriteTVShows(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyFavoriteTVTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesRatedMovies(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyRatedMoviesTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesRatedTVShows(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyRatedTVTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesWatchlistMovies(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyWatchListMoviesTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesWatchlistTVShows(accountId: Int, sessionId: String?): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages =
                    myAccountApiService.getMyWatchlistTVTotalPages(apiKey = API_KEY, accountId = accountId, sessionId = sessionId ,language = defaultLocale())?.execute()

                pages?.body()?.getTotalPages() ?: 1
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }
}